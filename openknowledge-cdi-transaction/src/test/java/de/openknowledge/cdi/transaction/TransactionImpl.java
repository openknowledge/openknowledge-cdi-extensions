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

package de.openknowledge.cdi.transaction;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.util.HashSet;
import java.util.Set;

/**
 * A transaction implementation used by {@link MockTransaction}. Requires further
 * improvements..
 *
 * @author Jens Schumann - open knowledge GmbH
 *
 */
class TransactionImpl implements Transaction {
  private int status = Status.STATUS_NO_TRANSACTION;
  private Set<XAResource> resources = new HashSet<XAResource>();


  private boolean rollbackOnly;

  private Xid xid;

  public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
    status = Status.STATUS_COMMITTING;
    try {
      for (XAResource res : resources) {
        res.prepare(xid);
      }

      for (XAResource res : resources) {
        res.commit(xid, false);
      }
    } catch (XAException e) {
      throw new SystemException(e.toString());
    }
    status = Status.STATUS_COMMITTED;

  }

  public boolean delistResource(XAResource xaRes, int flag) throws IllegalStateException, SystemException {
    resources.remove(xaRes);
    return true;
  }

  public boolean enlistResource(XAResource xaRes) throws RollbackException, IllegalStateException, SystemException {
    resources.add(xaRes);
    return true;
  }

  public int getStatus() throws SystemException {
    return status;
  }

  public void registerSynchronization(Synchronization sync) throws RollbackException, IllegalStateException, SystemException {
  }

  public void rollback() throws IllegalStateException, SystemException {
    status = Status.STATUS_ROLLING_BACK;
    try {
      for (XAResource res : resources) {
        res.rollback(xid);
      }
    } catch (XAException e) {
      throw new SystemException(e.toString());
    }
    status = Status.STATUS_ROLLEDBACK;
  }

  public void setRollbackOnly() throws IllegalStateException, SystemException {
    status =Status.STATUS_MARKED_ROLLBACK;
    rollbackOnly = true;
  }

  public void begin() {
    status = Status.STATUS_ACTIVE;
    xid = new Xid() {
      public int getFormatId() {
        return (int) System.currentTimeMillis();
      }

      public byte[] getGlobalTransactionId() {
        return ("mock-id" + getFormatId()).getBytes();
      }

      public byte[] getBranchQualifier() {
        return ("mock-branch" + getFormatId()).getBytes();
      }
    };
  }
}
