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

import java.lang.reflect.InvocationTargetException;

import javax.interceptor.InvocationContext;
import javax.transaction.TransactionRequiredException;

import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

/**
 * @author Arne Limburg - open knowledge GmbH
 */
public class MandatoryTransactionInterceptorTest {

  private StrictMockTransactionFactory transactionFactory;
  private MandatoryTransactionInterceptor interceptor;


  @Before
  public void setUp() throws Exception {
    transactionFactory = new StrictMockTransactionFactory();
    interceptor = new MandatoryTransactionInterceptor();
    interceptor.setUserTransaction(transactionFactory.getMockTransaction());
  }

  @Test(expected = TransactionRequiredException.class)
  public void noTransaction() throws Exception {
    transactionFactory.expectNoTransaction();
    transactionFactory.replay();
    try {
      InvocationContext ic = createNiceMock(InvocationContext.class);
      expect(ic.proceed()).andThrow(new ApplicationException());
      replay(ic);

      interceptor.checkTransaction(ic);
    } catch (InvocationTargetException e) {
      //due to OWB-575 we have to handle this 
      if (e.getTargetException() instanceof Error) {
        throw (Error) e.getTargetException();
      } else {
        throw (Exception) e.getTargetException();
      }
    } finally {
      transactionFactory.verify();
    }
  }

  @Test(expected = ApplicationException.class)
  public void activeTransaction() throws Exception {
    transactionFactory.expectActiveTransaction();
    transactionFactory.replay();
    try {
      InvocationContext ic = createNiceMock(InvocationContext.class);
      expect(ic.proceed()).andThrow(new ApplicationException());
      replay(ic);

      interceptor.checkTransaction(ic);
    } finally {
      transactionFactory.verify();
    }
  }

}
