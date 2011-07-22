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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;

import de.openknowledge.cdi.common.spi.DelegatingBean;

/**
 * This extension enables the injection of any object that has a constructor
 * with a single {@link String} parameter into injection points annotated with {@link Property}.
 * 
 * @author Arne Limburg
 */
public class PropertiesLoaderExtension implements Extension {

  private Set<Type> customTypes = new HashSet<Type>();
  private Bean<?> producePropertyBean;
  
  public <T> void registerCustomTypes(@Observes ProcessAnnotatedType<T> annotatedTypeEvent) {
    AnnotatedType<T> annotatedType = annotatedTypeEvent.getAnnotatedType();
    registerCustomType(annotatedType);
  }
  
  public void registerProducePropertyBean(@Observes ProcessBean<?> processBeanEvent) {
    Bean<?> bean = processBeanEvent.getBean();
    if (isProducePropertyBean(bean)) {
      producePropertyBean = bean;
    }
  }
  
  public void addBeans(@Observes AfterBeanDiscovery afterBeanDiscoveryEvent) {
    for (Type customType: customTypes) {
      afterBeanDiscoveryEvent.addBean(createProducePropertyBean(producePropertyBean, customType));
    }
  }
  
  private void registerCustomType(AnnotatedType<?> annotatedType) {
    for (AnnotatedField<?> field: annotatedType.getFields()) {
      if (isPropertyInjectionPoint(field)) {
        customTypes.add(field.getBaseType());
      }
    }
    for (AnnotatedMethod<?> method: annotatedType.getMethods()) {
      for (AnnotatedParameter<?> parameter: method.getParameters()) {
        if (isPropertyInjectionPoint(parameter)) {
          customTypes.add(parameter.getBaseType());
        }
      }
    }
    for (AnnotatedConstructor<?> constructor: annotatedType.getConstructors()) {
      for (AnnotatedParameter<?> parameter: constructor.getParameters()) {
        if (isPropertyInjectionPoint(parameter)) {
          customTypes.add(parameter.getBaseType());
        }
      }
    }
  }
  
  private static boolean isPropertyInjectionPoint(Annotated annotated) {
    return annotated.isAnnotationPresent(Property.class)
           && !isPrimitiveType(annotated.getBaseType())
           && hasStringConstructor(annotated.getBaseType());
  }
  
  private static boolean isPrimitiveType(Type type) {
    if (!(type instanceof Class)) {
      return false;
    }
    Class<?> classType = (Class<?>)type;
    return classType.isPrimitive();
  }
  
  private static boolean hasStringConstructor(Type type) {
    for (Constructor<?> constructor: PropertiesLoader.toClass(type).getConstructors()) {
      Class<?>[] parameterTypes = constructor.getParameterTypes();
      if (parameterTypes.length == 1 && String.class.equals(parameterTypes[0])) {
        return true;
      }
    }
    return false;
  }

  private boolean isProducePropertyBean(Bean<?> bean) {
    if (bean.getTypes().size() > 1) {
      return false;
    }
    if (!bean.getTypes().contains(Object.class)) {
      return false;
    }
    for (Annotation annotation: bean.getQualifiers()) {
      if (annotation.annotationType().equals(Property.class)) {
        return true;
      }
    }
    return false;
  }
  
  private <T> Bean<T> createProducePropertyBean(Bean<T> bean, Type type) {
    return new ProducePropertyBean<T>(bean, type);
  }

  private class ProducePropertyBean<T> extends DelegatingBean<T> {

    private Type type;
    
    public ProducePropertyBean(Bean<T> delegateBean, Type type) {
      super(delegateBean);
      this.type = type;
    }

    @Override
    public Set<Type> getTypes() {
      return Collections.singleton(type);
    }
  }
}
