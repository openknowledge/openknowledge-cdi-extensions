/*
 * Copyright open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.openknowledge.cdi.common.property.source;

import java.beans.Introspector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import de.openknowledge.cdi.common.annotation.Order;
import de.openknowledge.cdi.common.property.Property;
import de.openknowledge.cdi.common.property.PropertySource;

/**
 * Default implementation of our property provider. The default implementation
 * detects all available {@link de.openknowledge.cdi.common.property.source.PropertySourceLoader}
 * an uses {@link de.openknowledge.cdi.common.property.source.PropertySourceLoader#supports(String)}
 * to identify the responsible {@link de.openknowledge.cdi.common.property.source.PropertySourceLoader}
 * for a given source name.
 * <p/>
 * Unless specified, the lookup for an {@link de.openknowledge.cdi.common.property.source.PropertySourceLoader}
 * will happen in an undefined order. You may define the lookup order by supplying orders using
 * {@link de.openknowledge.cdi.common.annotation.Order}.
 * <p/>
 * Source names  may contain system properties (e.g. ${java.io.tmpdir}} and
 * will be automatically replaced before starting the internal loader lookup and property retrieval.
 * <p/>
 * Property files may contain self references to properties from the same property source or system properties.
 *
 * @author Arne Limburg - open knowledge GmbH
 * @author Jens Schumann - open knowledge GmbH
 * @version $Revision: 7662 $
 */

public class DefaultPropertyProvider implements PropertyProvider {

  public static final String PROPERTIES_FILE_EXTENSION = ".properties";

  public static final String CLASSPATH_SCHEME = "classpath";
	
  @Inject
  @Any
  private Instance<PropertySourceLoader> supportedSources;

  private Map<URI, Properties> properties = new ConcurrentHashMap<URI, Properties>();
  private Map<InjectionPoint, URI> sourceNameMap = new ConcurrentHashMap<InjectionPoint, URI>();

  private List<PropertySourceLoader> sourceLoaders = new ArrayList<PropertySourceLoader>();

  public String getPropertyValue(InjectionPoint injectionPoint) {
  
    Property property = injectionPoint.getAnnotated().getAnnotation(Property.class);

    Properties properties = getProperties(getSource(injectionPoint, property));

    return properties.getProperty(property.name(), getDefaultValue(properties, property));

  }

  public Properties getPropertyValues(InjectionPoint wildCard) {
    // TODO cache it.
    Property property = wildCard.getAnnotated().getAnnotation(Property.class);
    String wildcardName = property.name();
    if (!wildcardName.endsWith("*")) {
      throw new IllegalArgumentException("You need to specify a wildcard to access properties. Missing wildcard in " +
        wildcardName);
    }

    URI source = getSource(wildCard, property);

    Properties p = new Properties();

    if (source != null) {
      String keyPrefix = wildcardName.substring(0, wildcardName.length() - 1);
      Properties allProperties = getProperties(source);
      for (Object o : allProperties.keySet()) {
        if (String.valueOf(o).startsWith(keyPrefix)) {
          p.put(o, allProperties.get(o));
        }
      }
    }

    return p;
  }
  
  protected String getDefaultValue(Properties properties, Property property) {
    if (property.defaultValue().length() == 0) {
      return null;
    }
    return expandPropertyValue(properties, property.defaultValue());
  }

  protected URI getSource(InjectionPoint aInjectionPoint, Property property) {
    if (sourceNameMap.containsKey(aInjectionPoint)) {
      return sourceNameMap.get(aInjectionPoint);
    }

    URI source = extractSource(aInjectionPoint, property);
    if (source != null) {
      sourceNameMap.put(aInjectionPoint, source);
    }

    return source;
  }

  protected URI extractSource(InjectionPoint injectionPoint, Property property) {
    URI propertySource = extractFromProperty(injectionPoint, property);
    if (propertySource != null) {
      return propertySource;
    }
    URI classSource = extractSourceFromClass(injectionPoint.getBean().getBeanClass());
    if (classSource != null) {
      return classSource;
    }
    URI packageSource = extractSourceFromPackage(injectionPoint.getBean().getBeanClass().getPackage().getName());
    if (packageSource != null) {
      return packageSource;
    }
    return extractDefaultSource(injectionPoint.getBean().getBeanClass());
  }

  protected URI extractFromProperty(InjectionPoint injectionPoint, Property property) {
    if (property.source().length() == 0) {
      return null;
    }
    return toUri(injectionPoint.getBean().getBeanClass().getPackage(), property.source());
  }
  
  protected URI extractSourceFromClass(Class<?> beanClass) {
    if (!beanClass.isAnnotationPresent(PropertySource.class)) {
      return null;
    }
    return toUri(beanClass.getPackage(), beanClass.getAnnotation(PropertySource.class).value());
  }

  protected URI extractSourceFromPackage(String packageName) {
    Package pkg = Package.getPackage(packageName);
    if (pkg != null && pkg.isAnnotationPresent(PropertySource.class)) {
      return toUri(pkg, pkg.getAnnotation(PropertySource.class).value());
    } else {
      int index = packageName.lastIndexOf('.');
      if (index > 0) {
        return extractSourceFromPackage(packageName.substring(0, index));
      } else {
        return null;
      }
    }
  }

  protected URI extractDefaultSource(Class<?> beanClass) {
    String defaultName = Introspector.decapitalize(beanClass.getSimpleName()) + PROPERTIES_FILE_EXTENSION;
    return toUri(beanClass.getPackage(), defaultName);
  }

  protected URI toUri(Package pkg, String source) {
    source = expandSourceSystemProperties(source);
    try {
      URI uri = new URI(source);
      if (uri.getScheme() != null && !CLASSPATH_SCHEME.equals(uri.getScheme())) {
        return uri;
      }
      return new URI(uri.getScheme(),
                     uri.getUserInfo(),
                     uri.getHost(),
                     uri.getPort(),
                     resolve(pkg, uri.getPath()),
                     uri.getQuery(),
                     uri.getFragment());
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }
  
  protected String resolve(Package pkg, String path) {
    if (isAbsolute(path)) {
      return path;
    } else {
      String packageName = pkg.getName();
      int capacity = packageName.length() + path.length() + 2;
      StringBuilder pathBuilder = new StringBuilder(capacity);
      pathBuilder.append('/');
      pathBuilder.append(packageName.replace('.', '/'));
      pathBuilder.append('/');
      pathBuilder.append(path);
      return pathBuilder.toString();
    }
  }
  
  protected boolean isAbsolute(String source) {
    return source.charAt(0) == '/';
  }

  protected Properties getProperties(URI source) {
    Properties p = properties.get(source);
    return p != null ? p : loadPropertiesFromLoader(source);
  }


  protected synchronized Properties loadPropertiesFromLoader(URI source) {
    for (PropertySourceLoader sourceLoader : sourceLoaders) {
      if (sourceLoader.supports(source)) {
        Properties p = sourceLoader.load(source);
        for (Object key : p.keySet()) {
          p.put(key, expandPropertyValue(p, String.valueOf(p.get(key))));
        }

        // cache result
        properties.put(source, p);
        return p;
      }
    }
    throw new IllegalArgumentException("Unsupported source reference " + source);
  }


  protected String expandSourceSystemProperties(String value) {
    int i = value.indexOf("${");
    if (i > 0) {
      int last = value.indexOf("}", i);
      if (last > 0) {
        String placeholder = value.substring(i + 2, last);
        value = value.substring(0, i) + replaceSourceSystemProperty(placeholder) + value.substring(last + 1);
        return expandSourceSystemProperties(value);
      }
    }

    return value;
  }

  protected String replaceSourceSystemProperty(String placeHolder) {
    Object value = System.getProperties().get(placeHolder);
    if (value == null) {
      return "!" + placeHolder + "!";
    } else {
      return String.valueOf(value);
    }
  }

  protected String expandPropertyValue(Properties p, String value) {
    int i = value.indexOf("${");
    if (i >= 0) {
      int last = value.indexOf("}", i);
      if (last > 0) {
        String placeholder = value.substring(i + 2, last);
        value = value.substring(0, i) + replaceProperty(p, placeholder) + value.substring(last + 1);
        return expandPropertyValue(p, value);
      }
    }

    return value;
  }


  protected String replaceProperty(Properties p, String placeHolder) {
    Object value = p.get(placeHolder);
    if (value == null) {
      value = System.getProperties().get(placeHolder);
    }

    if (value == null) {
      return "!" + placeHolder + "!";
    } else {
      return String.valueOf(value);
    }
  }


  @PostConstruct
  protected void init() {
    List<PropertySourceLoader> unsorted = new ArrayList<PropertySourceLoader>();

    for (PropertySourceLoader newSourceLoader : supportedSources) {
      Order newLoaderOrderAnnotation = newSourceLoader.getClass().getAnnotation(Order.class);
      if (newLoaderOrderAnnotation != null) {
        int newLoaderOrder = newLoaderOrderAnnotation.value();
        boolean added = false;
        for (int i = 0; i < sourceLoaders.size(); i++) {
          Order currentLoaderAnnotation = sourceLoaders.get(i).getClass().getAnnotation(Order.class);

          if (newLoaderOrder <= currentLoaderAnnotation.value()) {
            sourceLoaders.add(i, newSourceLoader);
            added = true;
            break;
          }
        }
        if (!added) {
          sourceLoaders.add(newSourceLoader);
        }
      } else {
        // remember
        unsorted.add(0, newSourceLoader);
      }
    }

    // prepend all unordered loaders
    sourceLoaders.addAll(0, unsorted);
  }
}
