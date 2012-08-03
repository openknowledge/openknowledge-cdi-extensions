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

package de.openknowledge.cdi.scope;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Singleton;

/**
 * The Context for custom scopes marked with {@link Begin} or {@link End}.
 *
 * @author Arne Limburg - open knowledge GmbH (arne.limburg@openknowledge.de)
 */
@Singleton
public class DefaultDestroyableContext extends AbstractContext implements DestroyableContext {

  private Bean<?> parentBean;
  private Class<? extends Annotation> scope;
  private BeanManager beanManager;
  private Map<Object, Map<Contextual<?>, Instance<?>>> instances = new HashMap<Object, Map<Contextual<?>, Instance<?>>>();

  public DefaultDestroyableContext(Bean<?> parentBean, Class<? extends Annotation> scope, BeanManager beanManager) {
    if (parentBean == null) {
      throw new IllegalArgumentException("bean may not be null");
    }
    if (scope == null) {
      throw new IllegalArgumentException("scope may not be null");
    }
    if (beanManager == null) {
      throw new IllegalArgumentException("beanManager may not be null");
    }
    this.parentBean = parentBean;
    this.scope = scope;
    this.beanManager = beanManager;
  }

  @Override
  public Class<? extends Annotation> getScope() {
    return this.scope;
  }

  @Override
  public boolean isActive() {
    return getParentBeanInstance(null) != null;
  }

  public void destroy(Object destroyingBeanInstance) {
    Map<Contextual<?>, Instance<?>> instances = this.instances.get(destroyingBeanInstance);
    if (instances == null) {
      return;
    }
    for (Instance<?> instance : instances.values()) {
      instance.destroy();
    }
    this.instances.remove(destroyingBeanInstance);
  }
  
  protected Map<Contextual<?>, Instance<?>> getContextualMap(CreationalContext<?> creationalContext) {
    Object parentBeanInstance = getParentBeanInstance(creationalContext);
    if (parentBeanInstance == null) {
      return null;
    }
    Map<Contextual<?>, Instance<?>> instances = this.instances.get(parentBeanInstance);
    if (instances == null) {
      instances = new HashMap<Contextual<?>, Instance<?>>();
      this.instances.put(parentBeanInstance, instances);
    }
    return instances;
  }

  private <T> T getParentBeanInstance(CreationalContext<T> creationalContext) {
    if (creationalContext == null && this.parentBean.getScope().equals(Dependent.class)) {
      creationalContext = this.beanManager.createCreationalContext(this.<T>getDestroyingBean());
    }
    Context outerContext = this.beanManager.getContext(this.parentBean.getScope());
    T destroyingBeanInstance = outerContext.get(this.<T>getDestroyingBean());
    if (destroyingBeanInstance == null && creationalContext != null) {
      destroyingBeanInstance = outerContext.get(this.<T>getDestroyingBean(), creationalContext);
    }
    return destroyingBeanInstance;
  }

  @SuppressWarnings("unchecked")
  private <T> Bean<T> getDestroyingBean() {
    return (Bean<T>) this.parentBean;
  }
}
