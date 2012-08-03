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

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceUnit;

/**
 * @author Arne Limburg - open knowledge GmbH
 */
public class JpaInjectionTestBean {

  @PersistenceContext
  private EntityManagerFactory defaultFieldInjectedEntityManagerFactory;
  @PersistenceUnit
  private EntityManager defaultFieldInjectedEntityManager;
  private EntityManagerFactory defaultMethodInjectedEntityManagerFactory;
  private EntityManager defaultMethodInjectedEntityManager;
  @PersistenceContext(unitName = "custom", type = PersistenceContextType.EXTENDED)
  private EntityManagerFactory customFieldInjectedEntityManagerFactory;
  @PersistenceUnit(unitName = "custom")
  private EntityManager customFieldInjectedEntityManager;
  private EntityManagerFactory customMethodInjectedEntityManagerFactory;
  private EntityManager customMethodInjectedEntityManager;

  @PersistenceContext
  public void setDefaultEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
    defaultMethodInjectedEntityManagerFactory = entityManagerFactory;
  }

  @PersistenceUnit
  public void setDefaultEntityManager(EntityManager entityManager) {
    defaultMethodInjectedEntityManager = entityManager;
  }

  @PersistenceContext(unitName = "custom", type = PersistenceContextType.EXTENDED)
  public void setCustomEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
    customMethodInjectedEntityManagerFactory = entityManagerFactory;
  }

  @PersistenceUnit(unitName = "custom")
  public void setCustomEntityManager(EntityManager entityManager) {
    customMethodInjectedEntityManager = entityManager;
  }

  public EntityManagerFactory getDefaultFieldInjectedEntityManagerFactory() {
    return defaultFieldInjectedEntityManagerFactory;
  }

  public EntityManager getDefaultFieldInjectedEntityManager() {
    return defaultFieldInjectedEntityManager;
  }

  public EntityManagerFactory getDefaultMethodInjectedEntityManagerFactory() {
    return defaultMethodInjectedEntityManagerFactory;
  }

  public EntityManager getDefaultMethodInjectedEntityManager() {
    return defaultMethodInjectedEntityManager;
  }

  public EntityManagerFactory getCustomFieldInjectedEntityManagerFactory() {
    return customFieldInjectedEntityManagerFactory;
  }

  public EntityManager getCustomFieldInjectedEntityManager() {
    return customFieldInjectedEntityManager;
  }

  public EntityManagerFactory getCustomMethodInjectedEntityManagerFactory() {
    return customMethodInjectedEntityManagerFactory;
  }

  public EntityManager getCustomMethodInjectedEntityManager() {
    return customMethodInjectedEntityManager;
  }
}
