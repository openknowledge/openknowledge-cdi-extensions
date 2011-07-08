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
import java.util.Arrays;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Scope;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * The interceptor that manages the end of a {@link DestroyableContext}.
 *
 * @author Arne Limburg - open knowledge GmbH (arne.limburg@openknowledge.de)
 */
@Interceptor
@End(Scope.class)
public class EndScopeInterceptor extends AbstractScopeInterceptor<End> {

  @AroundInvoke
  public Object endScope(InvocationContext context) throws Exception {
    try {
      return context.proceed();
    } finally {
      destroyContexts(context);
    }
  }

  @PreDestroy
  public void beanDestroyed(InvocationContext context) {
    destroyContexts(context);
  }

  @Override
  protected List<Class<? extends Annotation>> getValue(End annotation) {
    return Arrays.asList(annotation.value());
  }
}
