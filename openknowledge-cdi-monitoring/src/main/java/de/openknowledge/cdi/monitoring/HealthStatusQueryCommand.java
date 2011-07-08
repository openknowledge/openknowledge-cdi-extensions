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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This event collects the current health status of valid server
 * components. Observers should add their status.
 * <p/>
 * Tested components should register their current status using {@link #reportStatus(ComponentStatus)}.
 *
 * @author Jens Schumann - OpenKnowledge GmbH
 * @version $Revision: 7797 $
 */
public class HealthStatusQueryCommand {

  private Map<ComponentStatusType, Set<ComponentStatus>> status = new HashMap<ComponentStatusType, Set<ComponentStatus>>();


  public HealthStatusQueryCommand() {
    status.put(ComponentStatusType.ERROR, new HashSet<ComponentStatus>());
    status.put(ComponentStatusType.WARNING, new HashSet<ComponentStatus>());
    status.put(ComponentStatusType.OK, new HashSet<ComponentStatus>());
  }

  public void reportStatus(ComponentStatus aStatus) {
    status.get(aStatus.getStatus()).add(aStatus);
  }

  public boolean hasErrors() {
    return status.get(ComponentStatusType.ERROR).size() > 0;
  }

  public boolean hasWarnings() {
    return status.get(ComponentStatusType.WARNING).size() > 0;
  }

  public boolean isOK() {
    return !hasErrors() && !hasWarnings();
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    if (isOK()) {
      buffer.append("OK");
    } else {
      appendAll(buffer, status.get(ComponentStatusType.ERROR));
      appendAll(buffer, status.get(ComponentStatusType.WARNING));
      appendAll(buffer, status.get(ComponentStatusType.OK));
    }
    return buffer.toString();
  }

  private void appendAll(StringBuffer aBuffer, Set<ComponentStatus> aComponentStatuses) {
    for (ComponentStatus status : aComponentStatuses) {
      aBuffer.append(status);
      aBuffer.append(SystemUtils.LINE_SEPARATOR);
    }
  }
}
