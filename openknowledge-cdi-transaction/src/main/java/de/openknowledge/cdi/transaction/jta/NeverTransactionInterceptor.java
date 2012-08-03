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

import static javax.ejb.TransactionAttributeType.NEVER;

import javax.ejb.EJBException;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.SystemException;

/**
 * @author Arne Limburg - open knowledge GmbH
 */
@TransactionAttribute(NEVER)
@Interceptor
public class NeverTransactionInterceptor extends AbstractTransactionAttributeInterceptor {

  @AroundInvoke
  public Object checkTransaction(InvocationContext ic) throws Exception {
    checkTransactionActive();
    return ic.proceed();
  }

  private void checkTransactionActive() throws SystemException, EJBException {
    if (isTransactionActive()) {
      throw new EJBException("transaction is active, but transaction type is NEVER");
    }
  }
}
