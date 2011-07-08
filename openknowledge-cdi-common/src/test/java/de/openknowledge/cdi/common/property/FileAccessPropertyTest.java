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

import de.openknowledge.cdi.common.property.test.filebased.FilePropertyTestBean;
import de.openknowledge.cdi.test.CdiJunit4TestRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;

/**
 * @author Jens Schumann - open knowledge GmbH
 * @version $Revision$
 */
@RunWith(CdiJunit4TestRunner.class)
public class FileAccessPropertyTest {

  @Inject
  private FilePropertyTestBean testBean;

  @BeforeClass
  public static void setUp() {
    System.setProperty("property.test.path", "src/test/resources");
  }

  @AfterClass
  public static void tearDown() {
    System.setProperty("property.test.path", "");
  }


  @Test
  public void testProperties() {
    assertEquals("successful", testBean.getTestStringProperty());
    assertEquals("successful", testBean.getTestStringPropertyWithPlaceHolder());

  }
}
