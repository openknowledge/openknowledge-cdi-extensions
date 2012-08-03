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

/* $Id: DelegatingPersistenceUnit.java 7594 2011-06-30 21:19:28Z jens.schumann $ */

package de.openknowledge.cdi.jpa;

import javax.enterprise.util.AnnotationLiteral;

/**
 * An implementation of {@link PersistenceUnit} that delegates to a {@link javax.persistence.PersistenceUnit}.
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public class DelegatingPersistenceUnit extends AnnotationLiteral<PersistenceUnit> implements PersistenceUnit {

  private javax.persistence.PersistenceUnit delegate;

  public DelegatingPersistenceUnit(javax.persistence.PersistenceUnit persistenceUnit) {
    delegate = persistenceUnit;
  }

  public String name() {
    return delegate.name();
  }

  @Override
  public String unitName() {
    return delegate.unitName();
  }
}
