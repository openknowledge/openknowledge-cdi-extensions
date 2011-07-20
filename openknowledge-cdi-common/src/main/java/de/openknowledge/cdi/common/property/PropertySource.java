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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Defines the property source for a java package or type. Use
 * package-info.java to define the property source for a package and
 * all nested sub packages.
 * <p/>
 * The specified resource represents an arbitrary property
 * source. By default classpath or file system properties are supported.
 * You may add further property sources by implementing {@link de.openknowledge.cdi.common.property.PropertySource}.
 * See {@link de.openknowledge.cdi.common.property.PropertySource} for further details.
 * <p/>
 * Sources starting with <code>file://</code> are assumed to be located in a relative
 * or absolute path in the file system. See {@link de.openknowledge.cdi.common.property.source.FilePropertySourceLoader}.
 * Classpath sources are .
 * <p/>
 * All other sources are assumed to be classpath sources where sources starting with a <code>/</code>
 * are assumed to be absolute classpath sources (resolved against the default package) and all other
 * sources are assumed to be relative to the class where this annotation is present.
 * <p/>
 *
 * Examples:
 * <pre>
 *  application.properties                       - located in classpath in the package where this annotation is present
 *  resources/application.properties             - located in classpath relative to the package where this annotation is present
 *                                                 (i.e. when this annotation is present at de/openknowledge/package_info.java
 *                                                 the properties file is in package de.openknowledge.resources
 *  /application.properties                      - located in classpath in default package
 *  /de/openknowledge/application.properties     - located in classpath in package de.openknowledge
 *  file://application.properties                - located in file system relative to current application path
 *  file:///etc/application.properties           - located in a unix file system at /etc
 *  file://${config.path}/application.properties - located in ${config.path}. ${config.path} is a java system property.
 * </pre>
 *
 * @author Jens Schumann - open knowledge GmbH
 * @author Arne Limburg - open knowledge GmbH
 * @version $Revision: 7658 $
 */
@Retention(RUNTIME)
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Inherited
public @interface PropertySource {
  /**
   *
   * The path and/or name of the property file.
   *
   * @return A name. See javadoc for conventions.
   */
  String value();
}
