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

import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

import javax.interceptor.InvocationContext;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * @author Jens Schumann - open knowledge GmbH
 */
public class ReadOnlyInterceptorTest {

  private StrictMockTransactionFactory transactionFactory;
  private RequiredTransactionInterceptor txInterceptor;
  private ReadOnlyInterceptor readOnlyInterceptor;


  @Before
  public void setUp() throws Exception {
    transactionFactory = new StrictMockTransactionFactory();
    txInterceptor = new RequiredTransactionInterceptor();
    txInterceptor.setUserTransaction(transactionFactory.getMockTransaction());

    readOnlyInterceptor = new ReadOnlyInterceptor(transactionFactory.getMockTransaction());
  }

  @Test
  public void beginAndRollbackTransaction() throws Exception {
    transactionFactory.expectNoTransaction();
    transactionFactory.expectBeginTransaction();
    transactionFactory.expectSetRollbackOnly();
    transactionFactory.expectRollbackTransaction();
    transactionFactory.replay();


    try {

      // a mock that expects two proceed calls and returns the read only interceptor.

      final InvocationContext mock = createMock(InvocationContext.class);

      expect(mock.proceed()).andAnswer(new IAnswer<Object>() {
        public Object answer() throws Throwable {
          return readOnlyInterceptor.markReadOnly(mock);
        }
      });

      expect(mock.proceed()).andReturn(null);
      replay(mock);

      txInterceptor.applyTransaction(mock);

      verify(mock);
    } finally {
      transactionFactory.verify();
    }
  }

}
