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

package de.openknowledge.cdi.monitoring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A template that may be used within {@link HealthStatusQueryCommand}
 * implementations to catch non expected errors. Example usage: <p/>
 * <pre>
 *  public void validateSomething(@Observes HealthStatusQueryCommand event) {
 *
 *   new ValidationTemplate(Xyz.class) {
 *     public void doValidate(HealthStatusQueryCommand event) {
 *       // do component validating here
 *     }
 *   }
 * }
 * </pre>.
 * The template logs unexpected errors.
 *
 * @author Jens Schumann - OpenKnowledge GmbH
 * @version $Revision: 7797 $
 */
public abstract class ValidationTemplate {

  private static final Log LOG = LogFactory.getLog(ValidationTemplate.class);

  private Class componentClass;

  protected ValidationTemplate(Class aComponentClass) {
    componentClass = aComponentClass;
  }

  public void validate(HealthStatusQueryCommand event) {
    try {
      doValidate(event);
    } catch (Throwable t) {
      LOG.error("Validation error in " + componentClass.getSimpleName(), t);
      event.reportStatus(new ComponentStatus(componentClass, t));
    }
  }

  public abstract void doValidate(HealthStatusQueryCommand event);
}
