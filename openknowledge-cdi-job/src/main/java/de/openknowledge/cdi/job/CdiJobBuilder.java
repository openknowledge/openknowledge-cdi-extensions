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

import org.quartz.JobBuilder;

/**
 * @author Arne Limburg
 */
public class CdiJobBuilder {

  public static CdiJobBuilder newJob() {
    return new CdiJobBuilder();
  }

  public static JobBuilder newJob(Class<? extends Runnable> job) {
    return new CdiJobBuilder().ofType(job);
  }

  private CdiJobBuilder() {
  }

  public JobBuilder ofType(Class<? extends Runnable> job) {
    return JobBuilder.newJob(CdiJob.class)
      .usingJobData(CdiJob.JOB_CLASS_NAME_PROPERTY, job.getName());
  }
}
