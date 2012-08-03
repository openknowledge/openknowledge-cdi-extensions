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

package de.openknowledge.cdi.inject;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.openknowledge.cdi.test.CdiJunit4TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Arne Limburg - open knowledge GmbH
 */
@RunWith(CdiJunit4TestRunner.class)
public class CdiInjectorTest {

  @Test
  public void inject() {
    ApplicationScopedBean bean = new ApplicationScopedBean();
    assertFalse(bean.wasPostConstructCalled());
    assertFalse(bean.wasPreDestroyCalled());
    assertNull(bean.getDependentBean());

    CdiInjector<ApplicationScopedBean> injector = new CdiInjector<ApplicationScopedBean>();

    injector.inject(bean);
    assertTrue(bean.wasPostConstructCalled());
    assertFalse(bean.wasPreDestroyCalled());
    assertNotNull(bean.getDependentBean());
    assertTrue(bean.getDependentBean().wasPostConstructCalled());
    assertFalse(bean.getDependentBean().wasPreDestroyCalled());
  }

  @Test
  public void dispose() {
    ApplicationScopedBean bean = new ApplicationScopedBean();
    assertFalse(bean.wasPostConstructCalled());
    assertFalse(bean.wasPreDestroyCalled());
    assertNull(bean.getDependentBean());

    CdiInjector<ApplicationScopedBean> injector = new CdiInjector<ApplicationScopedBean>();

    injector.dispose(bean);
    assertFalse(bean.wasPostConstructCalled());
    assertTrue(bean.wasPreDestroyCalled());
    assertNull(bean.getDependentBean());
  }
}
 