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

import org.apache.commons.lang.ClassUtils;

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

  /**
   * Creates an object, assuming the object has a constructor that takes a {@link String} argument.
   * The type of the object is determined from the type of the specified injection point.
   * The value for the constructor argument of type {@link String} is taken from the {@link Property}
   * specified at the injection point.
   * 
   * @param injectionPoint the injection point to get the type and the property from
   * @return the value which's type depends on the type of the injection point
   */
  @Produces
  @Property(name = "any")
  public Object produceProperty(InjectionPoint injectionPoint) {
    Class<?> type = toClass(injectionPoint.getType());
    return newInstance(injectionPoint, getPropertyValue(injectionPoint, type), ClassUtils.primitiveToWrapper(type));
  }

  /**
   * Creates a {@link Character} object from the specified injection point.
   * The character value is taken from the {@link Property} specified at the injection point.
   * This method is needed in addition to {@link #produceProperty(InjectionPoint)} since
   * {@link Character} has no constructor that takes a {@link String} argument.
   * 
   * @param injectionPoint the injection point to get the property from
   * @return the character value
   */
  @Produces
  @Property(name = "any")
  public Character produceCharacterProperty(InjectionPoint injectionPoint) {
    Class<?> type = toClass(injectionPoint.getType());
    String value = getPropertyValue(injectionPoint, type);
    if (value.length() != 1) {
      throw buildIllegalArgumentException(injectionPoint, value, type, null);
    }
    return value.charAt(0);
  }

  protected String getPropertyValue(InjectionPoint injectionPoint, Class<?> targetType) {
    String value = provider.getPropertyValue(injectionPoint);
    assertPrimitiveNotNull(injectionPoint, value, targetType);
    currentValues.registerPropertyValue(injectionPoint, value);
    return value;
  }
  
  static Class<?> toClass(Type type) {
    if (type instanceof Class<?>) {
      return (Class<?>)type;
    } else if (type instanceof ParameterizedType) {
      return toClass(((ParameterizedType)type).getRawType());
    } else {
      throw new IllegalArgumentException("unsupported type for property injection: " + type);
    }
  }

  private <T> T newInstance(InjectionPoint injectionPoint, String value, Class<T> type) {
    if (value == null) {
      return null;
    }
    try {
      return type.getConstructor(String.class).newInstance(value);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(type.getName() + " must have String-constructor to be injected");
    } catch (IllegalAccessException e) {
      throw new IllegalArgumentException("String-constructor must be public");
    } catch (InstantiationException e) {
      throw buildIllegalArgumentException(injectionPoint, value, type, e);
    } catch (InvocationTargetException e) {
      throw buildIllegalArgumentException(injectionPoint, value, type, e.getTargetException());
    }
  }
  
  private void assertPrimitiveNotNull(InjectionPoint injectionPoint, String value, Class<?> expectedType) {
    if (expectedType.isPrimitive() && value == null) {
      throw buildIllegalArgumentException(injectionPoint, value, expectedType, null);
    }
  }

  private RuntimeException buildIllegalArgumentException(InjectionPoint injectionPoint,
                                                         String value,
                                                         Class<?> expectedType,
                                                         Throwable cause) {
    StringBuilder messageBuilder = new StringBuilder();
    messageBuilder.append("Unable to convert ");
    if (value == null) {
      messageBuilder.append("null value");
    } else {
      messageBuilder.append('"').append(value).append('"');
    }
    messageBuilder.append(" to ").append(expectedType.getName());
    messageBuilder.append(" for injection point ").append(injectionPoint);
    return new IllegalArgumentException(messageBuilder.toString(), cause);
  }
}
