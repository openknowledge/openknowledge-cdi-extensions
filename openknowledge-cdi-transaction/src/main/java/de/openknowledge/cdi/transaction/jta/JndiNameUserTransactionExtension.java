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

import de.openknowledge.cdi.common.spi.AbstractCdiBean;
import de.openknowledge.cdi.common.spi.SingletonBean;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;
import java.util.Set;

/**
 * Provides a via JNDI accessible UserTransaction as injectable resource. As soon as
 * this extension is activated
 *
 * @author Jens Schumann - open knowledge GmbH
 */
public class JndiNameUserTransactionExtension implements Extension {

  public static final String USER_TRANSACTION_JNDI_NAME = "java:comp/env/UserTransaction";

  private String userTransactionName = USER_TRANSACTION_JNDI_NAME;


  protected String getUserTransactionName() {
    return userTransactionName;
  }

  public void addLinkToResource(@Observes AfterBeanDiscovery event, BeanManager bm) {
    Set<Bean<?>> set = bm.getBeans(UserTransaction.class);

    if (set.size() == 0) {
      event.addBean(new AbstractCdiBean<UserTransaction>("userTransaction", UserTransaction.class, bm) {
        public UserTransaction create(CreationalContext<UserTransaction> userTransactionCreationalContext) {
          InitialContext ctx = null;
          try {
            ctx = new InitialContext();
            return (UserTransaction) ctx.lookup(getUserTransactionName());
          } catch (NamingException e) {
            throw new RuntimeException(e);
          } finally {
            try {
              if (ctx != null) {
                ctx.close();
              }
            } catch (NamingException e) {
              // ingore
            }
          }
        }

        public void destroy(UserTransaction instance, CreationalContext<UserTransaction> userTransactionCreationalContext) {

        }
      });
    }
  }
}
