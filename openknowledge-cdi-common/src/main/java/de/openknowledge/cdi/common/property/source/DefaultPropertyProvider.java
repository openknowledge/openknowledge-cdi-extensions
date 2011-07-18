/*
 * Copyright 2011 open knowledge GmbH
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

import de.openknowledge.cdi.common.annotation.Order;
import de.openknowledge.cdi.common.property.Property;
import de.openknowledge.cdi.common.property.PropertySource;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Default implementation of our property provider. The default implementation
 * detects all available {@link de.openknowledge.cdi.common.property.source.PropertySourceLoader}
 * an uses {@link de.openknowledge.cdi.common.property.source.PropertySourceLoader#supports(String)}
 * to identify the responsible {@link de.openknowledge.cdi.common.property.source.PropertySourceLoader}
 * for a given source name.
 * <p/>
 * Unless specified the lookup for an {@link de.openknowledge.cdi.common.property.source.PropertySourceLoader}
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

  @Inject
  @Any
  private Instance<PropertySourceLoader> supportedSources;

  private Map<String, Properties> properties = new ConcurrentHashMap<String, Properties>();
  private Map<InjectionPoint, String> sourceNameMap = new ConcurrentHashMap<InjectionPoint, String>();

  private List<PropertySourceLoader> sourceLoaders = new ArrayList<PropertySourceLoader>();

  public String getPropertyValue(InjectionPoint injectionPoint) {
    Property property = injectionPoint.getAnnotated().getAnnotation(Property.class);

    String source = getSource(injectionPoint, property);

    String value = null;

    if (source != null) {
      value = getProperties(source).getProperty(property.name());
    }

    if (value == null && !"".equals(property.defaultValue())) {
      return property.defaultValue();
    } else {
      return value;
    }
  }

  public Properties getPropertyValues(InjectionPoint wildCard) {
    // TODO cache it.
    Property property = wildCard.getAnnotated().getAnnotation(Property.class);
    String wildcardName = property.name();
    if (!wildcardName.endsWith("*")) {
      throw new IllegalArgumentException("You need to specify a wildcard to access properties. Missing wildcard in " +
        wildcardName);
    }

    String source = getSource(wildCard, property);

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

  protected String getSource(InjectionPoint aInjectionPoint, Property property) {
    if (sourceNameMap.containsKey(aInjectionPoint)) {
      return sourceNameMap.get(aInjectionPoint);
    }

    String source = extractSource(aInjectionPoint, property);
    if (source != null) {
      sourceNameMap.put(aInjectionPoint, source);
    }

    return source;
  }

  protected String extractSource(InjectionPoint injectionPoint, Property property) {
    String source = property.source();
    if (source.length() == 0) {
      source = extractSourceFromClass(injectionPoint.getBean().getBeanClass());
      if (source == null) {
        source = extractSourceFromPackage(injectionPoint.getBean().getBeanClass().getPackage().getName());
      }
    }

    return source;
  }

  protected String extractSourceFromClass(Class<?> beanClass) {
    PropertySource sourceAnn = beanClass.getAnnotation(PropertySource.class);
    if (sourceAnn != null) {
      return sourceAnn.value();
    }

    return null;
  }

  protected String extractSourceFromPackage(String packageName) {
    Package pkg = Package.getPackage(packageName);
    if (pkg != null && pkg.isAnnotationPresent(PropertySource.class)) {
      return pkg.getAnnotation(PropertySource.class).value();
    } else {
      int index = packageName.lastIndexOf('.');
      if (index > 0) {
        return extractSourceFromPackage(packageName.substring(0, index));
      } else {
        return null;
      }
    }
  }


  protected Properties getProperties(String source) {
    Properties p = properties.get(source);
    return p != null ? p : loadPropertiesFromLoader(source);
  }


  protected synchronized Properties loadPropertiesFromLoader(String source) {
    String expandedSourceName = expandSourceSystemProperties(source);

    for (PropertySourceLoader sourceLoader : sourceLoaders) {
      if (sourceLoader.supports(expandedSourceName)) {
        Properties p = sourceLoader.load(expandedSourceName);
        for (Object key : p.keySet()) {
          p.put(key, expandProperties(p, String.valueOf(p.get(key))));
        }

        // cache result
        properties.put(source, p);
        return p;
      }
    }
    throw new IllegalArgumentException("Unsupported source reference " + expandedSourceName);
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

  protected String expandProperties(Properties p, String value) {
    int i = value.indexOf("${");
    if (i >= 0) {
      int last = value.indexOf("}", i);
      if (last > 0) {
        String placeholder = value.substring(i + 2, last);
        value = value.substring(0, i) + replaceProperty(p, placeholder) + value.substring(last + 1);
        return expandProperties(p, value);
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
