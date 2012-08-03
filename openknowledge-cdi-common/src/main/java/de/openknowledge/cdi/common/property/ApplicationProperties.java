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

package de.openknowledge.cdi.common.property;

import javax.enterprise.inject.spi.InjectionPoint;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * This object represents once injected using cdi the
 * current set of application distinctProperties.
 * 
 * @author Jens Schumann - open knowledge GmbH
 * @version $Revision: 7682 $
 */
public class ApplicationProperties {

  private Set<ApplicationProperty> distinctProperties = new HashSet<ApplicationProperty>();
  private Set<String> distinctPropertyKeys = new HashSet<String>();

  public Collection<ApplicationProperty> getDistinctProperties() {
    return distinctProperties;
  }

  public Set<String> getDistinctPropertyKeys() {
    return distinctPropertyKeys;
  }


  public void addProperty(Property aProperty) {
    distinctPropertyKeys.add(aProperty.name());
    distinctProperties.add(new ApplicationProperty(aProperty.name()));
  }

  public void registerPropertyValue(InjectionPoint aInjectionPoint, String aValue) {
    Property property = aInjectionPoint.getAnnotated().getAnnotation(Property.class);
    for (ApplicationProperty appProperty : distinctProperties) {
      if (appProperty.getPropertyKey().equals(property.name())) {
        appProperty.registerInjectionPoint(aInjectionPoint, property, aValue);
      }
    }
  }
}
