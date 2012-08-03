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

import javax.enterprise.inject.spi.InjectionPoint;
import java.util.Properties;

/**
 * @author Jens Schumann - open knowledge GmbH
 * @version $Revision: 7662 $
 */

public interface PropertyProvider {

  /**
   *
   * Provides the current property value.
   *
   * @param injectionPoint The injection point.
   * @return The value, may be null.
   */
  public String getPropertyValue(InjectionPoint injectionPoint);

  /**
   *
   * Provide all properties for a property wild card (such as foo.*)
   *
   * @param wildCard The injection point for a wildcard.
   * @return The located properties. May be empty.
   */
  public Properties getPropertyValues(InjectionPoint wildCard);

}
