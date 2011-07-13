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

import de.openknowledge.cdi.common.property.source.PropertyProvider;
import de.openknowledge.cdi.common.qualifier.Current;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import java.util.Properties;

/**
 * Our property source.
 *
 * @author Arne Limburg - open knowledge GmbH
 * @author Jens Schumann - open knowledge GmbH
 */
@ApplicationScoped
public class PropertiesLoader {

  @Inject
  @Current
  private ApplicationProperties currentValues = new ApplicationProperties();


  @Inject
  private PropertyProvider provider;


  @Produces
  @Property(name = "any")
  public Properties produceWildcardProperties(InjectionPoint injectionPoint) {
    return provider.getPropertyValues(injectionPoint);
  }


  @Produces
  @Property(name = "any")
  public String produceStringProperty(InjectionPoint injectionPoint) {
    String value = provider.getPropertyValue(injectionPoint);

    currentValues.registerPropertyValue(injectionPoint, value);

    return value;
  }

  @Produces
  @Property(name = "any")
  public boolean produceBooleanProperty(InjectionPoint injectionPoint) {
    String value = produceStringProperty(injectionPoint);

    if (value == null) {
      throw new IllegalArgumentException("Unable to convert null value to an boolean for injection point " +
        injectionPoint.getAnnotated());
    }
    return Boolean.valueOf(value.trim());
  }

  @Produces
  @Property(name = "any")
  public int produceIntProperty(InjectionPoint injectionPoint) {
    String value = produceStringProperty(injectionPoint);

    if (value == null) {
      throw new IllegalArgumentException("Unable to convert null value to an integer for injection point " +
        injectionPoint.getAnnotated());
    }

    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Unable to convert'" + value + "' to an integer for injection point " +
        injectionPoint.getAnnotated());
    }
  }

  @Produces
  @Property(name = "any")
  public long produceLongProperty(InjectionPoint injectionPoint) {
    String value = produceStringProperty(injectionPoint);

    if (value == null) {
      throw new IllegalArgumentException("Unable to convert null value to an long for injection point " +
        injectionPoint.getAnnotated());
    }

    try {
      return Long.parseLong(value);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Unable to convert'" + value + "' to an long for injection point " +
        injectionPoint.getAnnotated());
    }
  }
}
