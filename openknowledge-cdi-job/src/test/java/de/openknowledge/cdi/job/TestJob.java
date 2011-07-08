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

package de.openknowledge.cdi.job;

import javax.enterprise.context.ApplicationScoped;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@ApplicationScoped
public class TestJob implements Job {

  private boolean isStarted = false;
  private boolean isFinished = false;

  public void setStarted() {
    isStarted = true;
  }

  public synchronized boolean isFinished() {
    if (isStarted) {
      try {
        wait();
      } catch (InterruptedException e) {
        // ignore and go on
      }
    }
    return isFinished;
  }

  @Override
  public synchronized void execute(JobExecutionContext executionContext) throws JobExecutionException {
    isFinished = true;
    notifyAll();
  }
}
