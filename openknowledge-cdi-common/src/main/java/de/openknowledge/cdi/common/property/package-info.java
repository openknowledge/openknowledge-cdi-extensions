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

/**
 * Property support for CDI beans.
 * <p/>
 * Simply mark an injectable parameter or field using {@link de.openknowledge.cdi.common.property.Property}
 * in order to retrieve its value from a property source. Examples:
 * <pre>
 *
 *  &#064;PropertySource("application.properties")
 *  public class MyCdiBean  {
 *
 *    &#064;Inject
 *    &#064;Property(name="myProperty")
 *    private String field;
 *
 *    &#064;Inject
 *    public void setOtherField(&#064;Property(name="otherProperty") {...}
 *
 *    &#064;Inject
 *    public void setNextField(&#064;Property(name="nextProperty", defaultValue="foo") {...}
 *
 *    &#064;Inject
 *    public void setMyField(&#064;Property(name="myProperty", source="localProperties.properties") {...}
 *
 *  }
 * </pre>
 * <p/>
 * All application supported properties are exposed through &#064;Current
 * {@link de.openknowledge.cdi.common.property.ApplicationProperties}. See below. 
 * <p/>
 * <pre>
 *  &#064;Inject
 *  &#064;Current
 *  private ApplicationProperties properties;
 * </pre>
 *
 */
package de.openknowledge.cdi.common.property;