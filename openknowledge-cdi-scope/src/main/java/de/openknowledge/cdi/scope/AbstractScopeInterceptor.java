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

package de.openknowledge.cdi.scope;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;

import de.openknowledge.cdi.common.spi.CorrespondingLiteral;

/**
 * Abstract class with the begin/end interceptors. Provides methods to receive the scope.
 *
 * @author Arne Limburg - open knowledge GmbH (arne.limburg@openknowledge.de)
 */
public abstract class AbstractScopeInterceptor<A extends Annotation> implements Serializable {

  @Inject
  @Any
  private Instance<DestroyableContext> destroyableContexts;

  private Class<A> annotationType;

  protected AbstractScopeInterceptor() {
    // we try to find the concrete class of the generic "A"

    Type actualType = getParameterizedType();
    this.annotationType = getActualClass(actualType);
  }


  @SuppressWarnings("unchecked")
  private Class<A> getActualClass(Type actualType) {
    if (!(actualType instanceof Class)) {
      throw new IllegalStateException("Generic type argument of " + AbstractScopeInterceptor.class.getName()
        + " must be actual class");
    }
    return (Class<A>) actualType;
  }

  private Type getParameterizedType() {
    Type abstractScopeInterceptorType = getAbstractScopeInterceptorType();
    if (!(abstractScopeInterceptorType instanceof ParameterizedType)) {
      throw new IllegalStateException(AbstractScopeInterceptor.class
        + " must be instantiated with generic type argument");
    }
    Type actualType = ((ParameterizedType) abstractScopeInterceptorType).getActualTypeArguments()[0];
    return actualType;
  }

  private Type getAbstractScopeInterceptorType() {
    return getAbstractScopeInterceptorType(getClass());
  }

  private Type getAbstractScopeInterceptorType(Class<?> superClass) {
    if (AbstractScopeInterceptor.class.equals(superClass.getSuperclass())) {
      return superClass.getGenericSuperclass();
    }
    throw new IllegalStateException(AbstractScopeInterceptor.class.getName() + " not found in type-hierarchy");
  }

  protected void destroyContexts(InvocationContext context) {
    for (Class<? extends Annotation> scope : getScopes(context)) {
      DestroyableContext destroyableContext = this.destroyableContexts.select(new CorrespondingLiteral(scope)).get();
      destroyableContext.destroy(context.getTarget());
    }
  }

  protected Set<Class<? extends Annotation>> getScopes(InvocationContext context) {
    Set<Class<? extends Annotation>> scopes = new HashSet<Class<? extends Annotation>>();
    if (context.getMethod() != null) {
      scopes.addAll(getScopes(context.getMethod()));
    } else {
      for (Method method : context.getTarget().getClass().getMethods()) {
        scopes.addAll(getScopes(method));
      }
    }
    if (context.getTarget() != null) {
      scopes.addAll(getScopes(context.getTarget().getClass()));
    }

    if (scopes.isEmpty()) {
      throw new IllegalStateException(this.annotationType.getName() + " annotation not found");
    }

    return scopes;
  }

  protected Set<Class<? extends Annotation>> getScopes(Method method) {
    A annotation = method.getAnnotation(this.annotationType);
    return annotation != null ? new HashSet<Class<? extends Annotation>>(getValue(annotation)) : Collections
      .<Class<? extends Annotation>>emptySet();
  }

  protected Set<Class<? extends Annotation>> getScopes(Class<?> targetClass) {
    if (targetClass == null) {
      return Collections.emptySet();
    }
    A annotation = targetClass.getAnnotation(this.annotationType);
    if (annotation != null) {
      return new HashSet<Class<? extends Annotation>>(getValue(annotation));
    }
    return getScopes(targetClass.getSuperclass());
  }

  protected abstract List<Class<? extends Annotation>> getValue(A annotation);
}
