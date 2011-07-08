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

import static javax.ejb.TransactionAttributeType.SUPPORTS;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.TransactionRequiredException;

/**
 * Interceptor for invocations with {@link javax.ejb.TransactionAttributeType#NOT_SUPPORTED}.
 * The actual suspension of the transaction is handled by the superclass. This class
 * takes care, that no new transaction is started then
 * by overriding {@link SupportsTransactionInterceptor#beginTransactionIfNeeded()}
 *
 * @author Arne Limburg - open knowledge GmbH
 */
@TransactionAttribute(SUPPORTS)
@Interceptor
public class SupportsTransactionInterceptor extends RequiredTransactionInterceptor {

  //Need to override since OWB does not search for @AroundInvoke on superclasses
  @AroundInvoke
  public Object applyTransaction(InvocationContext ic) throws Exception {
    return super.applyTransaction(ic);
  }

  protected boolean beginTransactionIfNeeded() throws TransactionRequiredException, NotSupportedException, SystemException {
    return false;
  }
}
