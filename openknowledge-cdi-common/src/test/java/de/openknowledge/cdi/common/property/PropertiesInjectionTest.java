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

import de.openknowledge.cdi.test.CdiJunit4TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

/**
 * @author Arne Limburg - open knowledge GmbH
 */
@RunWith(CdiJunit4TestRunner.class)
public class PropertiesInjectionTest {

  @Inject
  @Property(name = "testString", source = "/de/openknowledge/cdi/common/property/test.properties")
  private String stringProperty;

  @Inject
  @Property(name = "missingInFile", defaultValue = "defaultStringValue", source = "test.properties")
  private String stringPropertyWithDefaultValue;

  @Inject
  @Property(name = "testInt", source = "test.properties")
  private int intProperty;

  @Inject
  @Property(name = "missingInFile", defaultValue = "50", source = "test.properties")
  private int intPropertyWithDefaultValue;

  @Inject
  @Property(name = "testBoolean", source = "test.properties")
  private boolean booleanProperty;

  @Inject
  @Property(name = "testInt", source = "test.properties")
  private Integer integerProperty;

  @Inject
  @Property(name = "missingInFile", source = "test.properties")
  private Integer integerPropertyMissingInFile;

  @Test
  public void success() {
    assertEquals("successful", stringProperty);
    assertEquals("defaultStringValue", stringPropertyWithDefaultValue);

    assertEquals(50, intProperty);
    assertEquals(50, intPropertyWithDefaultValue);
    
    assertTrue(booleanProperty);

    assertEquals(Integer.valueOf(50), integerProperty);
    assertNull(integerPropertyMissingInFile);
  }
}
