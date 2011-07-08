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

package de.openknowledge.cdi.lifecycle;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Startup;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;


/**
 * @author Arne Limburg - open knowledge GmbH
 */
public class LifecycleExtension implements Extension {

  private Set<Bean<?>> startupBeans = new HashSet<Bean<?>>();

  public void registerStartupBean(@Observes ProcessBean<?> event) {
    if (event.getAnnotated().isAnnotationPresent(Startup.class)) {
      startupBeans.add(event.getBean());
    }
  }

  public void initializeBeans(@Observes AfterDeploymentValidation event, BeanManager beanManager) {
    for (Bean<?> startupBean : startupBeans) {
      CreationalContext<?> creationalContext = beanManager.createCreationalContext(startupBean);
      beanManager.getReference(startupBean, Object.class, creationalContext).toString(); //initializes the bean
    }
  }
}
