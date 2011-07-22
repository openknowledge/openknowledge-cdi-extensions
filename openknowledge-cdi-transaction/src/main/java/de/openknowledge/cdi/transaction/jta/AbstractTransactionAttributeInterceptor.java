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

import javax.ejb.ApplicationException;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.io.Serializable;

/**
 * Handles JTA transactions
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public abstract class AbstractTransactionAttributeInterceptor implements Serializable {

  @Inject
  private UserTransactionHolder utHolder;

  @Inject
  @Any
  private Instance<EntityManager> entityManagers;

  public TransactionManager getTransactionManager() throws NotSupportedException {
    if (!(utHolder.getUserTransaction() instanceof TransactionManager)) {
      throw new NotSupportedException("Cannot receive TransactionManager");
    }
    return (TransactionManager) utHolder.getUserTransaction();
  }

  public boolean isTransactionActive() throws SystemException {
    return utHolder.getUserTransaction().getStatus() == Status.STATUS_ACTIVE;
  }

  public boolean isTransactionMarkedRollback() throws SystemException {
    return utHolder.getUserTransaction().getStatus() == Status.STATUS_MARKED_ROLLBACK;
  }

  public void markTransactionRollbackOnly() throws SystemException {
    utHolder.getUserTransaction().setRollbackOnly();
  }

  public void beginTransaction() throws NotSupportedException, SystemException {
    utHolder.getUserTransaction().begin();
    // this should not be necessary
    if (entityManagers != null) {
      for (EntityManager manager : entityManagers) {
        manager.joinTransaction();
      }
    }
  }

  public void commitTransaction() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException {
    utHolder.getUserTransaction().commit();
  }

  public void rollbackTransaction() throws SystemException {
    utHolder.getUserTransaction().rollback();
  }

  protected void rollbackTransactionIfNeeded(boolean responsible, Exception exception) throws SystemException {
    if (isTransactionActive()) {
      ApplicationException applicationException = exception.getClass().getAnnotation(ApplicationException.class);
      if (applicationException == null || applicationException.rollback()) {
        utHolder.getUserTransaction().setRollbackOnly();
      }
    }
  }

  protected void handleTransactionEnd(boolean responsible) throws SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
    if (responsible) {
      if (isTransactionMarkedRollback()) {
        rollbackTransaction();
      } else {
        commitTransaction();
      }
    }
  }

  public void setUserTransaction(UserTransaction userTransaction) {
    this.utHolder = new DefaultUserTransactionHolder(userTransaction);
  }
}
