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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.spi.Context;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;

import de.openknowledge.cdi.common.qualifier.Corresponding;
import de.openknowledge.cdi.common.spi.CorrespondingLiteral;
import de.openknowledge.cdi.common.spi.SingletonBean;

/**
 * The DestroyableContextExtension. Scans for {@link Begin} and {@link End} annotations and creates the
 * {@link DefaultDestroyableContext} for every annotated class.
 *
 * @author Arne Limburg - open knowledge GmbH (arne.limburg@openknowledge.de)
 */
public class DestroyableContextExtension implements Extension {

  private Map<Class<? extends Annotation>, Bean<?>> destroyableBeans
    = new HashMap<Class<? extends Annotation>, Bean<?>>();

  public void registerBean(@Observes ProcessBean<?> beanEvent, BeanManager beanManager) {
    End end = getAnnotation(End.class, beanEvent.getAnnotated(), beanManager);
    if (end != null) {
      for (Class<? extends Annotation> scope : end.value()) {
        this.destroyableBeans.put(scope, beanEvent.getBean());
      }
    }

    Begin begin = getAnnotation(Begin.class, beanEvent.getAnnotated(), beanManager);
    if (begin != null) {
      for (Class<? extends Annotation> scope : begin.value()) {
        this.destroyableBeans.put(scope, beanEvent.getBean());
      }
    }
  }

  public void registerContexts(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager beanManager) {
    for (Map.Entry<Class<? extends Annotation>, Bean<?>> entry : this.destroyableBeans.entrySet()) {
      Context context = new DefaultDestroyableContext(entry.getValue(), entry.getKey(), beanManager);
      afterBeanDiscovery.addContext(context);
      Corresponding corresponding = new CorrespondingLiteral(entry.getKey());
      Bean<?> contextBean = new SingletonBean(entry.getKey().getName(), context, beanManager, corresponding);
      afterBeanDiscovery.addBean(contextBean);
    }
  }

  private <A extends Annotation> A getAnnotation(Class<A> annotationType, Annotated annotated, BeanManager beanManager) {
    A annotation = annotated.getAnnotation(annotationType);
    if (annotation != null) {
      return annotation;
    } else if (annotated instanceof AnnotatedType) {
      AnnotatedType<?> annotatedType = (AnnotatedType<?>) annotated;
      for (AnnotatedMethod<?> method : annotatedType.getMethods()) {
        annotation = method.getAnnotation(annotationType);
        if (annotation != null) {
          return annotation;
        }
      }
      return null;
    } else if (annotated instanceof AnnotatedField) {
      AnnotatedField<?> field = (AnnotatedField<?>) annotated;
      Annotated fieldType = beanManager.createAnnotatedType(field.getJavaMember().getType());
      return getAnnotation(annotationType, fieldType, beanManager);
    } else if (annotated instanceof AnnotatedMethod) {
      AnnotatedMethod<?> method = (AnnotatedMethod<?>) annotated;
      Annotated returnType = beanManager.createAnnotatedType(method.getJavaMember().getReturnType());
      return getAnnotation(annotationType, returnType, beanManager);
    } else {
      return null;
    }
  }
}
