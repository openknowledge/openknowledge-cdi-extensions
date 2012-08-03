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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
 * @author Arne Limburg - open knowledge GmbH
 * @version $Revision: 7659 $
 */
public class FilePropertySourceLoader extends AbstractPropertySourceLoader {

  public static final String FILE_SCHEME = "file";

  private static final Log LOG = LogFactory.getLog(FilePropertySourceLoader.class);

  public boolean supports(URI source) {
    return FILE_SCHEME.equals(source.getScheme());
  }

  public Properties load(URI resource) {
    Properties properties = new Properties();
    
    try {
      File file;
      if (resource.isOpaque()) {
        file = new File(resource.getSchemeSpecificPart());
      } else {
        file = new File(resource);
      }
      if (file.exists() && file.canRead()) {
        LOG.debug("Loading properties from file " + file);
        loadFromStream(properties, new FileInputStream(file));
      } else {
        LOG.debug("Unable to load properties from file " + file + ". File does not exist or is not readable.");
      }
    } catch (IOException e) {
      LOG.warn("Error loading properties from file resource " + resource + ": " + e.getMessage());
    }

    return properties;
  }
}
