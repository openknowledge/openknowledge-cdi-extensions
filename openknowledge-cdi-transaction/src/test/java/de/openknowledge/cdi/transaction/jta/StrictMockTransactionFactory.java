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

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.reset;

import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.easymock.EasyMock;

/**
 * @author Arne Limburg - open knowledge GmbH
 */
public class StrictMockTransactionFactory {

  private StrictMockTransaction mockTransaction;
  private Transaction suspendedTransaction;
  private boolean recording = false;

  public StrictMockTransaction getMockTransaction() {
    if (mockTransaction == null) {
      mockTransaction = createStrictMock(StrictMockTransaction.class);
      recording = true;
    }
    return mockTransaction;
  }

  public void expectNoTransaction() throws Exception {
    if (!recording) {
      reset(getMockTransaction());
      recording = true;
    }
    expect(mockTransaction.getStatus()).andReturn(Status.STATUS_NO_TRANSACTION).atLeastOnce();
  }

  public void expectActiveTransaction() throws Exception {
    if (!recording) {
      reset(getMockTransaction());
      recording = true;
    }
    expect(mockTransaction.getStatus()).andReturn(Status.STATUS_ACTIVE).anyTimes();
  }

  public void expectSuspendTransaction() throws Exception {
    if (!recording) {
      reset(getMockTransaction());
      recording = true;
    }
    suspendedTransaction = createStrictMock(Transaction.class);
    expect(mockTransaction.suspend()).andReturn(suspendedTransaction);
    expect(mockTransaction.getStatus()).andReturn(Status.STATUS_NO_TRANSACTION).anyTimes();
  }

  public void expectResumeTransaction() throws Exception {
    mockTransaction.resume(suspendedTransaction);
    expectLastCall();
    expect(mockTransaction.getStatus()).andReturn(Status.STATUS_ACTIVE).anyTimes();
  }

  public void expectBeginTransaction() throws Exception {
    if (!recording) {
      reset(getMockTransaction());
      recording = true;
    }
    mockTransaction.begin();
    expectLastCall();
    expect(mockTransaction.getStatus()).andReturn(Status.STATUS_ACTIVE).anyTimes();
  }

  public void expectCommitTransaction() throws Exception {
    if (!recording) {
      reset(getMockTransaction());
      recording = true;
    }
    mockTransaction.commit();
    expectLastCall();
    expect(mockTransaction.getStatus()).andReturn(Status.STATUS_COMMITTED).anyTimes();
  }

  public void expectSetRollbackOnly() throws Exception {
    if (!recording) {
      reset(getMockTransaction());
      recording = true;
    }
    mockTransaction.setRollbackOnly();
    expectLastCall().atLeastOnce();
    expect(mockTransaction.getStatus()).andReturn(Status.STATUS_MARKED_ROLLBACK).anyTimes();
  }

  public void expectRollbackTransaction() throws Exception {
    if (!recording) {
      reset(getMockTransaction());
      recording = true;
    }
    mockTransaction.rollback();
    expectLastCall();
    expect(mockTransaction.getStatus()).andReturn(Status.STATUS_ROLLEDBACK).anyTimes();
  }

  public void replay() {
    if (suspendedTransaction != null) {
      EasyMock.replay(suspendedTransaction);
    }
    EasyMock.replay(mockTransaction);
  }

  public void verify() {
    if (suspendedTransaction != null) {
      EasyMock.verify(suspendedTransaction);
    }
    EasyMock.verify(mockTransaction);
  }

  public static interface StrictMockTransaction extends UserTransaction, TransactionManager {
  }
}
