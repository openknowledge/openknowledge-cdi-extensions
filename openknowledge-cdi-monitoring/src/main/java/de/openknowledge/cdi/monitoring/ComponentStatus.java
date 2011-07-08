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

package de.openknowledge.cdi.monitoring;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;


/**
 * The component status indicates the current status of a component. Components
 * can be arbitrary software components identified by a name.
 *
 * @author Jens Schumann - OpenKnowledge GmbH
 * @version $Revision: 7797 $
 */
public class ComponentStatus {

  private ComponentStatusType status;
  private String component;
  private String statusMessage;


  public ComponentStatus(String aComponent) {
    component = aComponent;
    status = ComponentStatusType.OK;
  }

  public ComponentStatus(Class aClazz) {
    this(aClazz.getSimpleName());
  }

  public ComponentStatus(String aComponent, ComponentStatusType aStatus, String aStatusMessage) {
    component = aComponent;
    status = aStatus;
    statusMessage = aStatusMessage;
  }

  public ComponentStatus(Class aClazz, ComponentStatusType aStatus, String aStatusMessage) {
    this(aClazz.getSimpleName(), aStatus, aStatusMessage);
  }

  public ComponentStatus(String aComponent, Throwable t) {
    status = ComponentStatusType.ERROR;
    component = aComponent;
    statusMessage = serializeError(t);
  }

  public ComponentStatus(Class aClazz, Throwable t) {
    this(aClazz.getSimpleName(), t);
  }

  public ComponentStatus(String aComponent, String aStatusMessage, Throwable t) {
    status = ComponentStatusType.ERROR;
    component = aComponent;
    statusMessage = aStatusMessage + " - " + serializeError(t);
  }

  public ComponentStatus(Class aClazz, String aStatusMessage, Throwable t) {
    this(aClazz.getSimpleName(), aStatusMessage, t);
  }

  public ComponentStatusType getStatus() {
    return status;
  }

  public String getComponent() {
    return component;
  }

  public String getStatusMessage() {
    return statusMessage;
  }

  public boolean isOK() {
    return status == ComponentStatusType.OK;
  }

  public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append(component);
    sb.append(" - ");
    sb.append(status);
    if (statusMessage != null) {
      sb.append(" (");
      int i = statusMessage.indexOf("\n");
      if (i > 0) {
        sb.append(statusMessage.substring(0, i));
      } else {
        sb.append(statusMessage);
      }
      sb.append(")");
    }
    return sb.toString();
  }

  protected String serializeError(Throwable aT) {
    return ExceptionUtils.getMessage(aT) + SystemUtils.LINE_SEPARATOR + ExceptionUtils.getStackTrace(aT);
  }
}


