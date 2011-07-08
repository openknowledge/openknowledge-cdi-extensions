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

package de.openknowledge.cdi.test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.inject.OWBInjector;
import org.apache.webbeans.spi.ContainerLifecycle;
import org.apache.webbeans.spi.ContextsService;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * A Cdi junit runner. Adds a system property called <code>junit.test</code>
 * with the value <code>"true"</code> as system property.
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public class CdiJunit4TestRunner extends BlockJUnit4ClassRunner {

  public static final String JUNIT_TEST = "junit.test";
  private Class<?> testType;
  private OWBInjector owbInjector;

  public CdiJunit4TestRunner(Class<?> type) throws InitializationError {
    super(type);
    testType = type;
  }

  protected Object createTest() throws Exception {
    System.setProperty(JUNIT_TEST, "true");
    ContainerLifecycle lifecycle = WebBeansContext.currentInstance().getService(ContainerLifecycle.class);
    lifecycle.startApplication(testType);
    BeanManager beanManager = lifecycle.getBeanManager();
    Set<Bean<?>> testBeans = new HashSet<Bean<?>>(beanManager.getBeans(testType));
    //filter out subclasses
    for (Iterator<Bean<?>> i = testBeans.iterator(); i.hasNext(); ) {
      if (!i.next().getBeanClass().equals(testType)) {
        i.remove();
      }
    }
    if (testBeans.isEmpty()) {
      Object test = super.createTest();
      owbInjector = new OWBInjector();
      owbInjector.inject(test);
      return test;
    }
    Bean<?> testBean = beanManager.resolve(testBeans);
    CreationalContext<?> creationalContext = beanManager.createCreationalContext(testBean);
    return beanManager.getReference(testBean, testType, creationalContext);
  }

  protected Statement methodBlock(FrameworkMethod method) {
    return new OpenWebBeansStopApplicationStatement(super.methodBlock(method));
  }

  protected Statement withPotentialTimeout(FrameworkMethod method, Object test, Statement next) {
    return super.withPotentialTimeout(method, test, new RequestScopedStatement(next));
  }

  public class OpenWebBeansStopApplicationStatement extends Statement {

    private Statement delegate;

    public OpenWebBeansStopApplicationStatement(Statement statement) {
      delegate = statement;
    }

    @Override
    public void evaluate() throws Throwable {
      try {
        delegate.evaluate();
      } finally {
        System.clearProperty(JUNIT_TEST);
        if (owbInjector != null) {
          owbInjector.destroy();
          owbInjector = null;
        }
        ContainerLifecycle lifecycle = WebBeansContext.currentInstance().getService(ContainerLifecycle.class);
        lifecycle.stopApplication(testType);
      }
    }
  }

  public class RequestScopedStatement extends Statement {

    private Statement next;
    
    public RequestScopedStatement(Statement next) {
      this.next = next;
    }
    
    @Override
    public void evaluate() throws Throwable {
      ContextsService contextsService = WebBeansContext.currentInstance().getContextsService();
      try {
        contextsService.startContext(RequestScoped.class, null);
        next.evaluate();
      } finally {
        contextsService.endContext(RequestScoped.class, null);
      }
    }
  }
}
