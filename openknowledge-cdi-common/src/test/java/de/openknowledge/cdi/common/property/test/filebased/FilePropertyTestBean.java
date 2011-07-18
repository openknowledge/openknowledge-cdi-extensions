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

package de.openknowledge.cdi.common.property.test.filebased;

import de.openknowledge.cdi.common.property.Property;

import javax.inject.Inject;

/**
 * @author Jens Schumann - open knowledge GmbH
 * @version $Revision$
 */
public class FilePropertyTestBean {

  @Inject
  @Property(name = "testString", source = "file:target/FileAccessPropertyTest.properties")
  private String testStringProperty;

  @Inject
  @Property(name = "testString", source = "file:${property.test.path}/FileAccessPropertyTest.properties")
  private String testStringPropertyWithPlaceHolder;

  public String getTestStringProperty() {
    return testStringProperty;
  }

  public String getTestStringPropertyWithPlaceHolder() {
    return testStringPropertyWithPlaceHolder;
  }
}
