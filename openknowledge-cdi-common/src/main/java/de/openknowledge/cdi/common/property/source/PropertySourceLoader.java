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

import java.net.URI;
import java.util.Properties;

/**
 *
 * A property source loader loads properties from an arbitrary
 * source such as property files (in classpath or file system) or databases.
 * <p/>
 * A Property Source Loader may hint to the property loader system that it should be used before
 * other property loaders using the {@link de.openknowledge.cdi.common.annotation.Order} annotation.
 * <p/>
 * Property loaders will be instantiated once.
 *
 * @author Jens Schumann - open knowledge GmbH
 * @author Arne Limburg - open knowledge GmbH
 * @version $Revision: 7659 $
 */
public interface PropertySourceLoader {

  /**
   * 
   * Resolves the specified resource name against the specified package.
   * 
   * @param pkg the package where the resource was specified 
   * @param resourceName the resource name
   * @return the resolved resource name
   */
  public URI resolve(Package pkg, URI resourceName);
  
  /**
   *
   * Indicate whether the given resource name is supported or not.
   *
   * @param resourceName The resource name.
   * @return  True if the source name can be loaded.
   */
  public boolean supports(URI resourceName);

  /**
   *
   * Load properties from the given resource.
   *
   * @param resourceName The name/path/identification of the resource.
   * @return The detected properties. May be empty.
   */
  public Properties load(URI resourceName);

}
