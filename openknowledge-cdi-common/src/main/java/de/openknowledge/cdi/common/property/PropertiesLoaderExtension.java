/*
 * Copyright open knowledge GmbH
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

import static de.openknowledge.cdi.common.property.PropertiesLoader.toClass;
import static org.apache.commons.lang.ClassUtils.primitiveToWrapper;
import static org.apache.commons.lang.ClassUtils.wrapperToPrimitive;
import static org.apache.commons.lang.Validate.notNull;

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

  private Set<Class<?>> customTypes = new HashSet<Class<?>>();
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
    for (Class<?> customType: customTypes) {
      afterBeanDiscoveryEvent.addBean(createProducePropertyBean(producePropertyBean, customType));
    }
  }
  
  private void registerCustomType(AnnotatedType<?> annotatedType) {
    for (AnnotatedField<?> field: annotatedType.getFields()) {
      if (isPropertyInjectionPoint(field)) {
        customTypes.add(primitiveToWrapper(toClass(field.getBaseType())));
      }
    }
    for (AnnotatedMethod<?> method: annotatedType.getMethods()) {
      for (AnnotatedParameter<?> parameter: method.getParameters()) {
        if (isPropertyInjectionPoint(parameter)) {
          customTypes.add(primitiveToWrapper(toClass(parameter.getBaseType())));
        }
      }
    }
    for (AnnotatedConstructor<?> constructor: annotatedType.getConstructors()) {
      for (AnnotatedParameter<?> parameter: constructor.getParameters()) {
        if (isPropertyInjectionPoint(parameter)) {
          customTypes.add(primitiveToWrapper(toClass(parameter.getBaseType())));
        }
      }
    }
  }
  
  private static boolean isPropertyInjectionPoint(Annotated annotated) {
    return annotated.isAnnotationPresent(Property.class)
           && isStringConstructorPresent(annotated.getBaseType());
  }
  
  private static boolean isStringConstructorPresent(Type type) {
    for (Constructor<?> constructor: primitiveToWrapper(toClass(type)).getConstructors()) {
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
  
  private <T> Bean<T> createProducePropertyBean(Bean<T> bean, Class<?> type) {
    return new ProducePropertyBean<T>(bean, type);
  }

  private class ProducePropertyBean<T> extends DelegatingBean<T> {

    private Class<?> type;
    
    public ProducePropertyBean(Bean<T> delegateBean, Class<?> type) {
      super(delegateBean);
      notNull(type);
      this.type = type;
    }

    @Override
    public Set<Type> getTypes() {
      return Collections.<Type>singleton(type);
    }

    @Override
    public boolean isNullable() {
      if (isWrapperType()) {
        return false;
      }
      return super.isNullable();
    }
    
    private boolean isWrapperType() {
      return wrapperToPrimitive(type) != null;
    }
  }
}
