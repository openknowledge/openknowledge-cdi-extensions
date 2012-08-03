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

package de.openknowledge.cdi.common.spi;

import java.lang.annotation.Annotation;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

/**
 * A bean that represents a singleton instance.
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public class SingletonBean extends AbstractCdiBean<Object> {

  private Object instance;

  public SingletonBean(String name, Object singletonInstance, BeanManager beanManager, Annotation... additionalQualifiers) {
    super(name, singletonInstance.getClass(), beanManager, additionalQualifiers);
    instance = singletonInstance;
  }

  public SingletonBean(String name, Object singletonInstance, Class<? extends Annotation> scope,
                       BeanManager beanManager, Annotation... additionalQualifiers) {
    super(name, singletonInstance.getClass(), beanManager, additionalQualifiers);
    instance = singletonInstance;
    setScope(scope);
  }

  @Override
  public Object create(CreationalContext<Object> creationalContext) {
    return instance;
  }

  @Override
  public void destroy(Object instance, CreationalContext<Object> creationalContext) {
    // singleton instance is never destroyed
  }
}
