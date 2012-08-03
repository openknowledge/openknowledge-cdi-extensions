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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.AmbiguousResolutionException;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import de.openknowledge.cdi.test.CdiJunit4TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Arne Limburg - open knowledge GmbH
 */
@RunWith(CdiJunit4TestRunner.class)
public class ObjectInstanceTest {

  @Inject
  private BeanManager beanManager;

  @Test(expected = AmbiguousResolutionException.class)
  public void get() {
    new ObjectInstance().get();
  }

  @Test
  public void isAmbiguous() {
    assertTrue(new ObjectInstance().isAmbiguous());
  }

  @Test
  public void isUnsatisfied() {
    assertFalse(new ObjectInstance().isUnsatisfied());
  }

  @Test
  public void iterator() {
//    This test does not work because of a bug in OpenWebBeans
//    Iterator<Object> iterator = new ObjectInstance().iterator();
//    assertTrue(iterator.hasNext());
//    assertNotNull(iterator.next());
  }

  @Test
  public void select() {
    Instance<Object> objectInstance = new ObjectInstance();
    Instance<Object> defaultInstance = objectInstance.select(new AnnotationLiteral<Default>() {
    });
    assertFalse(defaultInstance.isUnsatisfied());
    assertTrue(defaultInstance.isAmbiguous());

    Bean<ApplicationScopedBean> bean
      = (Bean<ApplicationScopedBean>) beanManager.resolve(beanManager.getBeans(ApplicationScopedBean.class));
    CreationalContext<ApplicationScopedBean> creationalContext = beanManager.createCreationalContext(bean);
    ApplicationScopedBean contextualReference
      = (ApplicationScopedBean) beanManager.getReference(bean, ApplicationScopedBean.class, creationalContext);

    ApplicationScopedBean applicationScopedBean = objectInstance.select(ApplicationScopedBean.class).get();
    assertEquals(contextualReference, applicationScopedBean);

//    This test does not work because of a bug in OpenWebBeans
//    applicationScopedBean = (ApplicationScopedBean)objectInstance.select(new TypeLiteral<ApplicationScopedBean>() {});
//    assertEquals(contextualReference, applicationScopedBean);
  }
}
