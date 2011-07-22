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

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.openknowledge.cdi.test.CdiJunit4TestRunner;

/**
 * @author Arne Limburg
 */
@RunWith(CdiJunit4TestRunner.class)
public class TypeConversionTest {

  private CustomObject customObject;
  
  @Test
  public void injectCustomObject() {
    assertNotNull(customObject);
    assertEquals("customValue", customObject.toString());
  }
  
  @Inject
  public void setCustomObject(@Property(name = "custom.object.name", defaultValue = "customValue") CustomObject customObject) {
    this.customObject = customObject;
  }
  
  public static class CustomObject {
    
    private String value;

    public CustomObject() {
      //
    }
    
    public CustomObject(String value) {
      this.value = value;
    }
    
    public String toString() {
      return value;
    }
  }
}
