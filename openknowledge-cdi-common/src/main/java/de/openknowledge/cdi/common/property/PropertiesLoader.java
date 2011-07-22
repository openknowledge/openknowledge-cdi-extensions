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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import de.openknowledge.cdi.common.property.source.PropertyProvider;
import de.openknowledge.cdi.common.qualifier.Current;

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
  public Object produceProperty(InjectionPoint injectionPoint) {
    Class<?> type = toClass(injectionPoint.getType());
    return newInstance(type, produceStringProperty(injectionPoint));
  }

  public String produceStringProperty(InjectionPoint injectionPoint) {
    String value = provider.getPropertyValue(injectionPoint);
    currentValues.registerPropertyValue(injectionPoint, value);
    return value;
  }

  @Produces
  @Property(name = "any")
  public byte produceByteProperty(InjectionPoint injectionPoint) {
    String value = produceStringProperty(injectionPoint);
    assertNotNull(injectionPoint, value, Byte.TYPE);
    try {
      return Byte.parseByte(value);
    } catch (NumberFormatException e) {
      throw buildIllegalArgumentException(injectionPoint, value, Byte.TYPE);
    }
  }

  @Produces
  @Property(name = "any")
  public short produceShortProperty(InjectionPoint injectionPoint) {
    String value = produceStringProperty(injectionPoint);
    assertNotNull(injectionPoint, value, Short.TYPE);
    try {
      return Short.parseShort(value);
    } catch (NumberFormatException e) {
      throw buildIllegalArgumentException(injectionPoint, value, Short.TYPE);
    }
  }

  @Produces
  @Property(name = "any")
  public int produceIntProperty(InjectionPoint injectionPoint) {
    String value = produceStringProperty(injectionPoint);
    assertNotNull(injectionPoint, value, Integer.TYPE);
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throw buildIllegalArgumentException(injectionPoint, value, Integer.TYPE);
    }
  }

  @Produces
  @Property(name = "any")
  public long produceLongProperty(InjectionPoint injectionPoint) {
    String value = produceStringProperty(injectionPoint);
    assertNotNull(injectionPoint, value, Long.TYPE);
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException e) {
      throw buildIllegalArgumentException(injectionPoint, value, Long.TYPE);
    }
  }

  @Produces
  @Property(name = "any")
  public float produceFloatProperty(InjectionPoint injectionPoint) {
    String value = produceStringProperty(injectionPoint);
    assertNotNull(injectionPoint, value, Float.TYPE);
    try {
      return Float.parseFloat(value);
    } catch (NumberFormatException e) {
      throw buildIllegalArgumentException(injectionPoint, value, Float.TYPE);
    }
  }

  @Produces
  @Property(name = "any")
  public double produceDoubleProperty(InjectionPoint injectionPoint) {
    String value = produceStringProperty(injectionPoint);
    assertNotNull(injectionPoint, value, Double.TYPE);
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      throw buildIllegalArgumentException(injectionPoint, value, Double.TYPE);
    }
  }

  @Produces
  @Property(name = "any")
  public boolean produceBooleanProperty(InjectionPoint injectionPoint) {
    String value = produceStringProperty(injectionPoint);
    assertNotNull(injectionPoint, value, Boolean.TYPE);
    return Boolean.parseBoolean(value);
  }

  @Produces
  @Property(name = "any")
  public char produceCharProperty(InjectionPoint injectionPoint) {
    String value = produceStringProperty(injectionPoint);
    assertNotNull(injectionPoint, value, Character.TYPE);
    if (value.length() != 1) {
      throw buildIllegalArgumentException(injectionPoint, value, Character.TYPE);
    }
    return value.charAt(0);
  }

  private Class<?> toClass(Type type) {
    if (type instanceof Class<?>) {
      return (Class<?>)type;
    } else if (type instanceof ParameterizedType) {
      return (Class<?>)((ParameterizedType)type).getRawType();
    } else {
      throw new IllegalArgumentException("unsupported type for property injection: " + type);
    }
  }

  private <T> T newInstance(Class<T> type, String value) {
    try {
      return type.getConstructor(String.class).newInstance(value);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(type.getName() + " must have String-constructor to be injected");
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException("String-constructor must be public");
    } catch (InstantiationException e) {
      throw new IllegalStateException(e);
    } catch (InvocationTargetException e) {
      if (e.getTargetException() instanceof RuntimeException) {
        throw (RuntimeException)e.getTargetException();
      } else if (e.getTargetException() instanceof Error) {
        throw (Error)e.getTargetException();
      } else {
        throw new IllegalArgumentException(e.getTargetException());
      }
    }
  }
  
  private void assertNotNull(InjectionPoint injectionPoint, String value, Class<?> expectedType) {
    if (value == null) {
      throw buildIllegalArgumentException(injectionPoint, value, expectedType);
    }
  }

  private RuntimeException buildIllegalArgumentException(InjectionPoint injectionPoint,
                                                         String value,
                                                         Class<?> expectedType) {
    StringBuilder messageBuilder = new StringBuilder();
    messageBuilder.append("Unable to convert ");
    if (value == null) {
      messageBuilder.append("null value");
    } else {
      messageBuilder.append('"').append(value).append('"');
    }
    messageBuilder.append(" to ").append(expectedType.getName());
    messageBuilder.append(" for injection point ").append(injectionPoint);
    return new IllegalArgumentException(messageBuilder.toString());
  }
}
