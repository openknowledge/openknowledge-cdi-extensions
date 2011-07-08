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
package de.openknowledge.cdi.inject;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * This class provides support for CDI injection into unmanaged objects.
 *
 * @author Arne Limburg - open knowledge GmbH (arne.limburg@openknowledge.de)
 */
public class CdiInjector<T> {

  public void inject(T object) {
    BeanManager beanManager = BeanManagerProvider.getBeanManager();
    CreationalContext<T> creationalContext = beanManager.createCreationalContext(null);
    AnnotatedType<T> annotatedType = beanManager.createAnnotatedType((Class<T>) object.getClass());
    InjectionTarget<T> injectionTarget = beanManager.createInjectionTarget(annotatedType);
    injectionTarget.inject(object, creationalContext);
    injectionTarget.postConstruct(object);
  }

  public void dispose(T object) {
    BeanManager beanManager = BeanManagerProvider.getBeanManager();
    AnnotatedType<T> annotatedType = beanManager.createAnnotatedType((Class<T>) object.getClass());
    InjectionTarget<T> injectionTarget = beanManager.createInjectionTarget(annotatedType);
    injectionTarget.preDestroy(object);
    injectionTarget.dispose(object);
  }
}
