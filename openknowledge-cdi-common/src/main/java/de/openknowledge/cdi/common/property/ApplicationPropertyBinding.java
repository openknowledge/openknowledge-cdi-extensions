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

/**
* @author Jens Schumann - open knowledge GmbH
* @version $Revision: 7682 $
*/
public class ApplicationPropertyBinding {
  private String binding;
  private String value;
  private boolean isDefault;


  public ApplicationPropertyBinding(String aName, Property aProperty, String currentValue) {
    binding = aName;
    if (aProperty.defaultValue().length() != 0) {
      isDefault = aProperty.defaultValue().equals(currentValue);
    }

    if (aProperty.mask()) {
      value = "*****";
    } else {
      value = currentValue;
    }
  }

  public String getBinding() {
    return binding;
  }

  public String getValue() {
    return value;
  }

  public boolean isDefault() {
    return isDefault;
  }
}
