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

package de.openknowledge.cdi.transaction.jta;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import de.openknowledge.cdi.common.spi.DelegatingAnnotatedMethod;
import de.openknowledge.cdi.common.spi.DelegatingAnnotatedType;

/**
 * Replaces {@link javax.ejb.TransactionAttribute} with {@link TransactionAttribute}.
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public class TransactionAttributeExtension implements Extension {

  public <T> void replaceTransactionAttribute(@Observes ProcessAnnotatedType<T> event) {
    if (isTransactionAttributeAnnotationPresent(event.getAnnotatedType())) {
      event.setAnnotatedType(new TransactionAttributeAnnotatedType<T>(event.getAnnotatedType()));
    }
  }

  private boolean isTransactionAttributeAnnotationPresent(AnnotatedType<?> annotatedType) {
    if (annotatedType.isAnnotationPresent(javax.ejb.TransactionAttribute.class)) {
      return true;
    }
    for (AnnotatedMethod<?> method : annotatedType.getMethods()) {
      if (method.isAnnotationPresent(javax.ejb.TransactionAttribute.class)) {
        return true;
      }
    }
    return false;
  }

  private static class TransactionAttributeAnnotatedType<T> extends DelegatingAnnotatedType<T> {

    private TransactionAttribute defaultTransactionAttribute;

    public TransactionAttributeAnnotatedType(AnnotatedType<T> delegateType) {
      super(delegateType);
      javax.ejb.TransactionAttribute transactionAttribute = delegateType.getAnnotation(javax.ejb.TransactionAttribute.class);
      if (transactionAttribute != null) {
        defaultTransactionAttribute = new DelegatingTransactionAttribute(transactionAttribute);
        removeAnnotation(javax.ejb.TransactionAttribute.class);
      }
    }

    protected AnnotatedMethod<? super T> processAnnotatedMethod(AnnotatedMethod<? super T> method) {
      javax.ejb.TransactionAttribute transactionAttribute = method.getAnnotation(javax.ejb.TransactionAttribute.class);
      if (transactionAttribute == null && defaultTransactionAttribute == null) {
        return method;
      } else if (transactionAttribute != null) {
        return createDelegatingMethod(method, transactionAttribute);
      } else {
        return createDelegatingMethod(method, defaultTransactionAttribute);
      }
    }

    private <M> AnnotatedMethod<M> createDelegatingMethod(AnnotatedMethod<M> method, javax.ejb.TransactionAttribute transactionAttribute) {
      return createDelegatingMethod(method, new DelegatingTransactionAttribute(transactionAttribute));
    }

    private <M> AnnotatedMethod<M> createDelegatingMethod(AnnotatedMethod<M> method, TransactionAttribute transactionAttribute) {
      DelegatingAnnotatedMethod<M> delegatingMethod
        = new DelegatingAnnotatedMethod<M>((AnnotatedType<M>) this, method);
      delegatingMethod.addAnnotation(transactionAttribute);
      delegatingMethod.removeAnnotation(javax.ejb.TransactionAttribute.class);
      return delegatingMethod;
    }
  }
}
