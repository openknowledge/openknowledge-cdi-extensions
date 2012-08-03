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

import static javax.ejb.TransactionAttributeType.REQUIRED;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.TransactionRequiredException;

/**
 * @author Arne Limburg - open knowledge GmbH
 */
@TransactionAttribute(REQUIRED)
@Interceptor
public class RequiredTransactionInterceptor extends AbstractTransactionAttributeInterceptor {

  @AroundInvoke
  public Object applyTransaction(InvocationContext ic) throws Exception {
    boolean transactionStarted = beginTransactionIfNeeded();
    try {
      Object result = ic.proceed();
      return result;
    } catch (Exception e) {
      rollbackTransactionIfNeeded(transactionStarted, e);
      throw e;
    } finally {
      handleTransactionEnd(transactionStarted);
    }
  }

  protected boolean beginTransactionIfNeeded() throws TransactionRequiredException, NotSupportedException, SystemException {
    if (!isTransactionActive()) {
      beginTransaction();
      return true;
    }
    return false;
  }
}
