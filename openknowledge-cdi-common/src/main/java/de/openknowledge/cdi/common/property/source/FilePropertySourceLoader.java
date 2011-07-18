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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * A property source loader that loads properties from a file. Supports
 * relative or absolute files.
 * <p/>
 * Processes sources that start with <code>file://</code>.
 * <p/>
 * Order value is {@link Integer#MAX_VALUE} - 1.
 *
 * @author Jens Schumann - open knowledge GmbH
 * @version $Revision: 7659 $
 */
@Order(Integer.MAX_VALUE - 1)
public class FilePropertySourceLoader extends AbstractPropertySourceLoader {

  public static final String FILE_PREFIX = "file:";

  private static final Log LOG = LogFactory.getLog(FilePropertySourceLoader.class);

  public boolean supports(String source) {
    return source.startsWith(FILE_PREFIX);
  }

  public Properties load(String resourceName) {
    Properties properties = new Properties();
    File absoluteFile = null;

    try {
      String path = resourceName.substring(FILE_PREFIX.length());
      absoluteFile = new File(path).getAbsoluteFile();
      if (absoluteFile.exists() && absoluteFile.canRead()) {
        LOG.debug("Loading properties from file " + absoluteFile);
        loadFromStream(properties, new FileInputStream(absoluteFile));
      } else {
        LOG.debug("Unable to load properties from file " + absoluteFile + ". File does not exist or is not readable.");
      }
    } catch (IOException e) {
      LOG.warn("Error loading properties from file resource " + absoluteFile + ": " + e.getMessage());
    }

    return properties;
  }
}
