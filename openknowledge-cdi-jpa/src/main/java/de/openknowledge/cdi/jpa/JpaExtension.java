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

package de.openknowledge.cdi.jpa;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import de.openknowledge.cdi.common.spi.DelegatingAnnotatedField;
import de.openknowledge.cdi.common.spi.DelegatingAnnotatedMethod;
import de.openknowledge.cdi.common.spi.DelegatingAnnotatedParameter;
import de.openknowledge.cdi.common.spi.DelegatingAnnotatedType;
import de.openknowledge.cdi.common.spi.InjectLiteral;

/**
 * This extension handles the injection of {@link javax.persistence.PersistenceUnit}s and {@link PersistenceContext}s
 * by adding {@link javax.inject.Inject}
 * and {@link de.openknowledge.cdi.jpa.PersistenceUnit} or {@link de.openknowledge.cdi.jpa.PersistenceContext}
 * to according annotated fields and methods.
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public class JpaExtension implements Extension {

  public <T> void processJpaAnnotations(@Observes ProcessAnnotatedType<T> annotatedTypeEvent) {
    if (annotatedTypeEvent.getAnnotatedType().isAnnotationPresent(Entity.class)
      || annotatedTypeEvent.getAnnotatedType().isAnnotationPresent(Embeddable.class)) {
      annotatedTypeEvent.veto(); // JPA objects are no CDI beans
    }
    AnnotatedType<T> annotatedType = annotatedTypeEvent.getAnnotatedType();
    if (isJpaAnnotationPresent(annotatedType)) {
      annotatedTypeEvent.setAnnotatedType(new JpaAnnotatedType<T>(annotatedType));
    }
  }

  private boolean isJpaAnnotationPresent(AnnotatedType<?> annotatedType) {
    for (AnnotatedField<?> field : annotatedType.getFields()) {
      if (isJpaAnnotationPresent(field)) {
        return true;
      }
    }
    for (AnnotatedConstructor<?> constructor : annotatedType.getConstructors()) {
      if (isJpaAnnotationPresent(constructor)) {
        return true;
      }
    }
    for (AnnotatedMethod<?> method : annotatedType.getMethods()) {
      if (isJpaAnnotationPresent(method)) {
        return true;
      }
    }
    return false;
  }

  private boolean isJpaAnnotationPresent(Annotated annotated) {
    return annotated.isAnnotationPresent(PersistenceUnit.class) || annotated.isAnnotationPresent(PersistenceContext.class);
  }

  private static class JpaAnnotatedType<T> extends DelegatingAnnotatedType<T> {

    public JpaAnnotatedType(AnnotatedType<T> delegate) {
      super(delegate);
    }

    @Override
    protected AnnotatedField<? super T> processAnnotatedField(AnnotatedField<? super T> field) {
      if (field.isAnnotationPresent(PersistenceContext.class)) {
        return createPersistenceContextField(field, field.getAnnotation(PersistenceContext.class));
      } else if (field.isAnnotationPresent(PersistenceUnit.class)) {
        return createPersistenceUnitField(field, field.getAnnotation(PersistenceUnit.class));
      } else {
        return super.processAnnotatedField(field);
      }
    }

    @Override
    protected AnnotatedMethod<? super T> processAnnotatedMethod(AnnotatedMethod<? super T> method) {
      if (method.isAnnotationPresent(PersistenceContext.class)) {
        return createPersistenceContextMethod(method);
      } else if (method.isAnnotationPresent(PersistenceUnit.class)) {
        return createPersistenceUnitMethod(method);
      } else {
        return super.processAnnotatedMethod(method);
      }
    }

    @SuppressWarnings("unchecked")
    private <A> AnnotatedField<A> createPersistenceContextField(AnnotatedField<A> field, PersistenceContext delegate) {
      DelegatingAnnotatedField<A> delegateField = new DelegatingAnnotatedField<A>((AnnotatedType<A>) this, field);
      delegateField.removeAnnotation(delegate.annotationType());
      delegateField.addAnnotation(new DelegatingPersistenceContext(delegate));
      if (!field.isAnnotationPresent(Produces.class)) {
        delegateField.addAnnotation(new InjectLiteral());
      }
      return delegateField;
    }

    @SuppressWarnings("unchecked")
    private <A> AnnotatedField<A> createPersistenceUnitField(AnnotatedField<A> field, PersistenceUnit delegate) {
      DelegatingAnnotatedField<A> delegateField = new DelegatingAnnotatedField<A>((AnnotatedType<A>) this, field);
      delegateField.removeAnnotation(delegate.annotationType());
      delegateField.addAnnotation(new DelegatingPersistenceUnit(delegate));
      if (!field.isAnnotationPresent(Produces.class)) {
        delegateField.addAnnotation(new InjectLiteral());
      }
      return delegateField;
    }

    @SuppressWarnings("unchecked")
    private <A> AnnotatedMethod<A> createPersistenceContextMethod(AnnotatedMethod<A> method) {
      if (method.isAnnotationPresent(Produces.class)) {
        DelegatingAnnotatedMethod<A> delegatingMethod = new DelegatingAnnotatedMethod<A>((AnnotatedType<A>) this, method);
        PersistenceContext persistenceContext = method.getAnnotation(PersistenceContext.class);
        delegatingMethod.removeAnnotation(persistenceContext.annotationType());
        delegatingMethod.addAnnotation(new DelegatingPersistenceContext(persistenceContext));
        return delegatingMethod;
      } else {
        return new PersistenceContextMethod<A>((AnnotatedType<A>) this, method);
      }
    }

    @SuppressWarnings("unchecked")
    private <A> AnnotatedMethod<A> createPersistenceUnitMethod(AnnotatedMethod<A> method) {
      if (method.isAnnotationPresent(Produces.class)) {
        DelegatingAnnotatedMethod<A> delegatingMethod = new DelegatingAnnotatedMethod<A>((AnnotatedType<A>) this, method);
        PersistenceUnit persistenceUnit = method.getAnnotation(PersistenceUnit.class);
        delegatingMethod.removeAnnotation(persistenceUnit.annotationType());
        delegatingMethod.addAnnotation(new DelegatingPersistenceUnit(persistenceUnit));
        return delegatingMethod;
      } else {
        return new PersistenceUnitMethod<A>((AnnotatedType<A>) this, method);
      }
    }
  }

  private static class PersistenceContextMethod<T> extends DelegatingAnnotatedMethod<T> {

    private PersistenceContext persistenceContext;

    public PersistenceContextMethod(AnnotatedType<T> declaringType, AnnotatedMethod<T> delegate) {
      super(declaringType, delegate);
      persistenceContext = delegate.getAnnotation(PersistenceContext.class);
      removeAnnotation(persistenceContext.annotationType());
      if (!delegate.isAnnotationPresent(Produces.class)) {
        addAnnotation(new InjectLiteral());
      }
    }

    protected AnnotatedParameter<T> processAnnotatedParameter(AnnotatedParameter<T> annotatedParameter) {
      if (EntityManagerFactory.class.isAssignableFrom(toClass(annotatedParameter.getBaseType()))) {
        return new DelegatingAnnotatedParameter<T>(this, annotatedParameter, createPersistenceContextAnnotation());
      } else {
        return annotatedParameter;
      }
    }

    private DelegatingPersistenceContext createPersistenceContextAnnotation() {
      return new DelegatingPersistenceContext(persistenceContext);
    }
  }

  private static class PersistenceUnitMethod<T> extends DelegatingAnnotatedMethod<T> {

    private PersistenceUnit persistenceUnit;

    public PersistenceUnitMethod(AnnotatedType<T> declaringType, AnnotatedMethod<T> delegate) {
      super(declaringType, delegate);
      persistenceUnit = delegate.getAnnotation(PersistenceUnit.class);
      removeAnnotation(persistenceUnit.annotationType());
      if (!delegate.isAnnotationPresent(Produces.class)) {
        addAnnotation(new InjectLiteral());
      }
    }

    protected AnnotatedParameter<T> processAnnotatedParameter(AnnotatedParameter<T> annotatedParameter) {
      if (EntityManager.class.isAssignableFrom(toClass(annotatedParameter.getBaseType()))) {
        return new DelegatingAnnotatedParameter<T>(this, annotatedParameter, createPersistenceUnitAnnotation());
      } else {
        return annotatedParameter;
      }
    }

    private DelegatingPersistenceUnit createPersistenceUnitAnnotation() {
      return new DelegatingPersistenceUnit(persistenceUnit);
    }
  }

  private static Class<?> toClass(Type type) {
    if (type instanceof Class) {
      return (Class<?>) type;
    } else if (type instanceof ParameterizedType) {
      return toClass(((ParameterizedType) type).getRawType());
    } else {
      throw new IllegalArgumentException("Cannot convert type to class: " + type);
    }
  }
}
