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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Arne Limburg - open knowledge GmbH (arne.limburg@openknowledge.de)
 */
@ApplicationScoped
public class Parent {

  private boolean isScopeEnded = false;
  @Inject
  private TestScopedChild scopedBean;

  public TestScopedChild getScopedBean() {
    return this.scopedBean;
  }

  public boolean isEnded() {
    return this.isScopeEnded;
  }

  @End(TestScope.class)
  public void end() {
    this.isScopeEnded = true;
  }
}
