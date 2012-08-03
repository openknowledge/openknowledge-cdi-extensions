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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import de.openknowledge.cdi.test.CdiJunit4TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Arne Limburg - open knowledge GmbH (arne.limburg@openknowledge.de)
 */
@RunWith(CdiJunit4TestRunner.class)
public class DestroyableContextTest {

  @Inject
  private Parent scopingBean;
  @Inject
  private BeanManager beanManager;

  @Test
  public void inject() {
    Bean<TestScopedChild> testScopedBean = (Bean<TestScopedChild>) beanManager.resolve(beanManager.getBeans(TestScopedChild.class));
    CreationalContext<TestScopedChild> creationalContext = beanManager.createCreationalContext(testScopedBean);
    assertNotNull(this.scopingBean.getScopedBean());
    TestScopedChild scopedBean = (TestScopedChild) beanManager.getReference(testScopedBean, TestScopedChild.class, creationalContext);
    assertEquals(this.scopingBean.getScopedBean(), scopedBean);
    int hashCode = scopedBean.hashCode();
    this.scopingBean.end();
    assertTrue(this.scopingBean.isEnded());
    TestScopedChild newScopedBean = (TestScopedChild) beanManager.getReference(testScopedBean, TestScopedChild.class, creationalContext);
    assertNotSame(hashCode, newScopedBean.hashCode());
  }
}
