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

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

/**
 * @author Arne Limburg
 */
public class ThreadContext extends AbstractContext {

  private ThreadLocal<Map<Contextual<?>, Instance<?>>> contextualMaps = new ThreadLocal<Map<Contextual<?>,Instance<?>>>();
  
  @Override
  public Class<? extends Annotation> getScope() {
    return ThreadScoped.class;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  @Override
  protected Map<Contextual<?>, Instance<?>> getContextualMap(CreationalContext<?> creationalContext) {
    Map<Contextual<?>, Instance<?>> contextualMap = contextualMaps.get();
    if (contextualMap == null) {
      contextualMap = new HashMap<Contextual<?>, AbstractContext.Instance<?>>();
      contextualMaps.set(contextualMap);
    }
    return contextualMap;
  }

}


