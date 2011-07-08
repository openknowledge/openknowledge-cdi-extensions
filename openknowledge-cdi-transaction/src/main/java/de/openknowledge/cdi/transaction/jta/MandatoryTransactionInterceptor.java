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

import static javax.ejb.TransactionAttributeType.MANDATORY;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.SystemException;
import javax.transaction.TransactionRequiredException;

/**
 * Interceptor for invocations with {@link javax.ejb.TransactionAttributeType#MANDATORY}.
 *
 * @author Arne Limburg - open knowledge GmbH
 */
@TransactionAttribute(MANDATORY)
@Interceptor
public class MandatoryTransactionInterceptor extends AbstractTransactionAttributeInterceptor {

  @AroundInvoke
  public Object checkTransaction(InvocationContext ic) throws Exception {
    checkTransactionActive();
    return ic.proceed();
  }

  private void checkTransactionActive() throws TransactionRequiredException, SystemException {
    if (!isTransactionActive()) {
      throw new TransactionRequiredException();
    }
  }
}
