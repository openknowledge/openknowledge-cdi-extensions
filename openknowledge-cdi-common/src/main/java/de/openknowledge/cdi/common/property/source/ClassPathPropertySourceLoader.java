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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * The fallback or default property source handler loads property files from
 * classpath. Order value is {@link Integer#MAX_VALUE}.
 *
 * @author Jens Schumann - open knowledge GmbH
 * @version $Revision: 7659 $
 */
@Order(Integer.MAX_VALUE)
public class ClassPathPropertySourceLoader extends AbstractPropertySourceLoader {

  private static final Log LOG = LogFactory.getLog(ClassPathPropertySourceLoader.class);

  @Override
  public boolean supports(String source) {
    return true;
  }

  public Properties load(String resourceName) {
    Properties properties = new Properties();

    try {
      InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
      if (stream == null) {
        LOG.warn("Property file " + resourceName + " not found in classpath.");
      } else {
        LOG.debug("Loading properties from classpath " + resourceName);
        loadFromStream(properties, stream);
      }
    } catch (IOException e) {
      LOG.warn("Error loading properties from classpath resource " + resourceName + ": " + e.getMessage());
    }
    return properties;
  }
}
