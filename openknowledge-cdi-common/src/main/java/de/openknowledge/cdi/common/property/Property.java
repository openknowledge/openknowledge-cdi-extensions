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

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *
 * Indicates that the given property should be resolved
 * using a property source. See {@link de.openknowledge.cdi.common.property.PropertySource}
 * for supported sources.
 * <p/>
 * Properties will be automatically converted to java primitives
 * and Strings. Currently supported conversions are String , boolean, int and long.
 * <p/>
 * A group of properties may be access using a property wildcard. Example:
 * &#064;Inject @Property(name="all.for.namespace.*") private Properties prop;
 *
 * @author Arne Limburg - open knowledge GmbH
 * @author Jens Schumann - open knowledge GmbH
 * @version $Revision: 7658 $
 */
@Qualifier
@Retention(RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
public @interface Property {
  /**
   *
   * The property name.
   *
   * @return the name of the property.
   */
  @Nonbinding String name();

  /**
   *
   * A default value that should be used if the property
   * is not available in the property source.
   *
   * @return The default value. Keep empty for non existing default values.
   */
  @Nonbinding String defaultValue() default "";

  /**
   *
   * The source to use. Overrides globally defined
   * property sources. See {@link PropertySource}
   * for syntax and global property source declaration.
   *
   * The default value which is taken when this attribute is left blank
   * and no {@link PropertySource} annotation is present at the appropriate places
   * depends on the name of the class where this {@link Property} annotation is present:
   * It is a properties file within the same package and the name of the class
   * with a lower-case first letter and an appendix of .properties,
   * i.e. if the class is com.example.MyClass,
   * the default value of this attribute is /com/example/myClass.properties
   *
   * @return The property source.
   */
  @Nonbinding String source() default "";

  /**
   *
   * Mask default property value in {@link de.openknowledge.cdi.common.property.ApplicationProperties}.
   * Especially useful for passwords and other internal details.
   *
   * @return True to mask the property. Default is false.
   */
  @Nonbinding boolean mask() default false;

}
