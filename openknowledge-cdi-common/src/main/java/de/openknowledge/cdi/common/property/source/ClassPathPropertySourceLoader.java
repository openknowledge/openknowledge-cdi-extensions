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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * The fallback or default property source handler loads property files from
 * classpath. Order value is {@link Integer#MAX_VALUE}.
 *
 * @author Jens Schumann - open knowledge GmbH
 * @author Arne Limburg - open knowledge GmbH
 * @version $Revision: 7659 $
 */
public class ClassPathPropertySourceLoader extends AbstractPropertySourceLoader {

  public static final String CLASSPATH_SCHEME = "classpath";

  private static final Log LOG = LogFactory.getLog(ClassPathPropertySourceLoader.class);

  @Override
  public boolean supports(URI source) {
    return source.getScheme() == null || CLASSPATH_SCHEME.equals(source.getScheme());
  }

  public Properties load(URI resource) {
    Properties properties = new Properties();

    try {
      String resourceName = resource.getPath().substring(1);
      InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
      if (stream == null) {
        LOG.warn("Property file " + resource + " not found in classpath.");
      } else {
        LOG.debug("Loading properties from classpath " + resource);
        loadFromStream(properties, stream);
      }
    } catch (IOException e) {
      LOG.warn("Error loading properties from classpath resource " + resource + ": " + e.getMessage());
    }
    return properties;
  }
}
