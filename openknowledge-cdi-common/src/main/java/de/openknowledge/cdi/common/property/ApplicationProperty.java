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

package de.openknowledge.cdi.common.property;

import javax.enterprise.inject.spi.InjectionPoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents configurable application properties. The provided
 * details are property metadata that can be analyzed during application runtime.
 *
 * @author Jens Schumann - open knowledge GmbH
 * @version $Revision: 7684 $
 */
public class ApplicationProperty {
  private String propertyKey;
  private Map<String, ApplicationPropertyBinding> values = new HashMap<String, ApplicationPropertyBinding>();


  public ApplicationProperty(String aPropertyKey) {
    propertyKey = aPropertyKey;
  }

  public void registerInjectionPoint(InjectionPoint p, Property aProperty, String currentValue) {
    String binding = p.getMember().getDeclaringClass().getName() + "." + p.getMember().getName();
    if (values.containsKey(binding)) {
      return;
    }

    values.put(binding, new ApplicationPropertyBinding(binding, aProperty, currentValue));
  }


  public Collection<ApplicationPropertyBinding> getPropertyUsages() {
    return new ArrayList<ApplicationPropertyBinding>(values.values());
  }

  public String getPropertyKey() {
    return propertyKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ApplicationProperty that = (ApplicationProperty) o;

    if (propertyKey != null ? !propertyKey.equals(that.propertyKey) : that.propertyKey != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return propertyKey != null ? propertyKey.hashCode() : 0;
  }

}
