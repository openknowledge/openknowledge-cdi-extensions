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

import javax.enterprise.util.AnnotationLiteral;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceProperty;

/**
 * An implementation of {@link PersistenceContext} that delegates to a {@link javax.persistence.PersistenceContext}.
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public class DelegatingPersistenceContext extends AnnotationLiteral<PersistenceContext> implements PersistenceContext {

  private javax.persistence.PersistenceContext delegate;

  public DelegatingPersistenceContext(javax.persistence.PersistenceContext persistenceContext) {
    delegate = persistenceContext;
  }

  @Override
  public String name() {
    return delegate.name();
  }

  @Override
  public String unitName() {
    return delegate.unitName();
  }

  @Override
  public PersistenceContextType type() {
    return delegate.type();
  }

  @Override
  public PersistenceProperty[] properties() {
    return delegate.properties();
  }
}
