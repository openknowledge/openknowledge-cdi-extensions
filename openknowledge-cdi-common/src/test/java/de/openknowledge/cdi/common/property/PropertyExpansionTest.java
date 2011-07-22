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

import de.openknowledge.cdi.test.CdiJunit4TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

/**
 * @author Jens Schumann - open knowledge GmbH
 * @version $Revision$
 */
@RunWith(CdiJunit4TestRunner.class)
public class PropertyExpansionTest {

  @Inject
  @Property(name = "testSystemProperty", source = "/de/openknowledge/cdi/common/property/test.properties")
  private String systemProperty;


  @Inject
  @Property(name = "backwardsReference", source = "test.properties")
   private int backwardsReference;

  @Test
  public void testSystemPropertyExpansion() {
    assertEquals(System.getProperty("java.io.tmpdir"), systemProperty);
    assertEquals(50, backwardsReference);
  }
}
