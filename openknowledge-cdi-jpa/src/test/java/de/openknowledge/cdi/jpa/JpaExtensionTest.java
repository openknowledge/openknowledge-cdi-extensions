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

package de.openknowledge.cdi.jpa;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContextType;

import static org.easymock.EasyMock.*;

import static org.junit.Assert.*;

import de.openknowledge.cdi.test.CdiJunit4TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Arne Limburg - open knowledge GmbH
 */
@RunWith(CdiJunit4TestRunner.class)
public class JpaExtensionTest {

  @Inject
  private MockProducer mockProducer;
  @Inject
  private JpaInjectionTestBean testBean;

  @Test
  public void injection() {
    assertSame(mockProducer.defaultEntityManagerFactory, testBean.getDefaultFieldInjectedEntityManagerFactory());
    assertSame(mockProducer.defaultEntityManagerFactory, testBean.getDefaultMethodInjectedEntityManagerFactory());
    assertSame(mockProducer.defaultEntityManager, testBean.getDefaultFieldInjectedEntityManager());
    assertSame(mockProducer.defaultEntityManager, testBean.getDefaultMethodInjectedEntityManager());
    assertSame(mockProducer.customEntityManagerFactory, testBean.getCustomFieldInjectedEntityManagerFactory());
    assertSame(mockProducer.customEntityManagerFactory, testBean.getCustomMethodInjectedEntityManagerFactory());
    assertSame(mockProducer.customEntityManager, testBean.getCustomFieldInjectedEntityManager());
    assertSame(mockProducer.customEntityManager, testBean.getCustomMethodInjectedEntityManager());
  }

  @Singleton
  private static class MockProducer {
    @Produces
    @PersistenceContext
    private EntityManagerFactory defaultEntityManagerFactory = createMock(EntityManagerFactory.class);
    @Produces
    @PersistenceUnit
    private EntityManager defaultEntityManager = createMock(EntityManager.class);
    @Produces
    @PersistenceContext(unitName = "custom", type = PersistenceContextType.TRANSACTION)
    private EntityManagerFactory transactionalEntityManagerFactory = createMock(EntityManagerFactory.class);
    @Produces
    @PersistenceUnit(unitName = "transactional")
    private EntityManager transactionalEntityManager = createMock(EntityManager.class);
    @Produces
    @PersistenceContext(unitName = "custom", type = PersistenceContextType.EXTENDED)
    private EntityManagerFactory customEntityManagerFactory = createMock(EntityManagerFactory.class);
    @Produces
    @PersistenceUnit(unitName = "custom")
    private EntityManager customEntityManager = createMock(EntityManager.class);
  }
}
