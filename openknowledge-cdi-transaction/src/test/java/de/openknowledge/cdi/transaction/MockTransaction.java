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

package de.openknowledge.cdi.transaction;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

/**
 *
 * A mock user transaction implementation that may be used for testing
 * purposes.
 *
 * @author Jens Schumann - open knowledge GmbH
 * @version $Revision$
 *
 */
public class MockTransaction implements UserTransaction, TransactionManager {
  private static ThreadLocal<TransactionImpl> transaction = new ThreadLocal<TransactionImpl>();
  private static MockTransaction transactionManager = new MockTransaction();

  public static MockTransaction getTransactionManager() {
   return transactionManager;
  }


  public Transaction getTransaction() throws SystemException {
    return getInternalTx();
  }

  public void resume(Transaction tobj) throws InvalidTransactionException, IllegalStateException, SystemException {
    transaction.set((TransactionImpl) tobj);
  }

  public Transaction suspend() throws SystemException {
    try {
      return getInternalTx();
    } finally {
      transaction.set(new TransactionImpl());
    }
  }

  public void begin() throws NotSupportedException, SystemException {
    getInternalTx().begin();
  }

  public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
    getInternalTx().commit();
  }

  public void rollback() throws IllegalStateException, SecurityException, SystemException {
    getInternalTx().rollback();
  }

  public void setRollbackOnly() throws IllegalStateException, SystemException {
    getInternalTx().setRollbackOnly();
  }

  public int getStatus() throws SystemException {
    return getInternalTx().getStatus();
  }

  public void setTransactionTimeout(int seconds) throws SystemException {
    // ignored
  }

  public TransactionImpl getInternalTx() {
    if (transaction.get() == null) {
      transaction.set(new TransactionImpl());
    }

    return transaction.get();
  }
}
