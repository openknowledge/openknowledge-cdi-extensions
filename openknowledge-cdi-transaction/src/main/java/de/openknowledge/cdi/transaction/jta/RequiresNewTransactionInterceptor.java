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

package de.openknowledge.cdi.transaction.jta;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

/**
 * @author Arne Limburg - open knowledge GmbH
 */
@TransactionAttribute(REQUIRES_NEW)
@Interceptor
public class RequiresNewTransactionInterceptor extends RequiredTransactionInterceptor {

  @AroundInvoke
  public Object applyTransaction(InvocationContext ic) throws Exception {
    TransactionManager transactionManager = null;
    Transaction transaction = null;
    if (isTransactionActive()) {
      transactionManager = getTransactionManager();
      transaction = transactionManager.suspend();
    }
    try {
      return super.applyTransaction(ic);
    } finally {
      if (transactionManager != null && transaction != null) {
        transactionManager.resume(transaction);
      }
    }
  }
}
