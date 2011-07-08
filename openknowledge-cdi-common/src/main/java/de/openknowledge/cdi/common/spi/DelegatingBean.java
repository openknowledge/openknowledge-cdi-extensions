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

package de.openknowledge.cdi.common.spi;

import java.lang.annotation.Annotation;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * A bean that is operator-specific
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public class DelegatingBean<T> extends AbstractCdiBean<T> {

  private Bean<T> delegate;

  public DelegatingBean(Bean<T> delegateBean, BeanManager beanManager, Annotation... qualifiers) {
    super(delegateBean.getName(), delegateBean.getBeanClass(), beanManager, qualifiers);
    delegate = delegateBean;
  }

  @Override
  public T create(CreationalContext<T> creationalContext) {
    return delegate.create(creationalContext);
  }

  @Override
  public void destroy(T instance, CreationalContext<T> creationalContext) {
    delegate.destroy(instance, creationalContext);
  }
}
