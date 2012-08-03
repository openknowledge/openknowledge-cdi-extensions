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

/**
 *
 * This package enables the usage of {@link javax.ejb.TransactionAttribute}
 * for transaction management. It includes support for {@link javax.ejb.ApplicationException}.
 * <p/>
 * CDI-Transactions require a working JTA environment which should be available
 * within an application server or can be enabled using JTA implementations
 * such as bitronix.
 * <p/>
 * To enable TX management just use {@link javax.ejb.TransactionAttribute} on class
 * or method level. You may declare a transaction as read-only using the {@link de.openknowledge.cdi.transaction.ReadOnly}
 * annotation. The transaction will be marked as rollback only ensuring that the communication
 * with JTA resources will be rolled back in any case.
 * <p/>
 * You may use the test dependency of this package to add a CDI UserTransaction/TransactionManager
 * mockup facility. Simply add the archive to your test project in order to use injectable user transactions.
 *
 */
package de.openknowledge.cdi.transaction;