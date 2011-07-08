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

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.openknowledge.cdi.common.qualifier.Current;
import de.openknowledge.cdi.scope.End;
import de.openknowledge.cdi.scope.ThreadScoped;

/**
 * @author Arne Limburg
 */
@ThreadScoped
public class CdiJob implements org.quartz.Job {

  public static final String JOB_CLASS_NAME_PROPERTY = Job.class.getName() + ".class.name";
  
  @Inject @Job
  private Instance<Runnable> jobInstance;
  private JobExecutionContext executionContext;
  
  protected CdiJob() {
	  //for cdi
  }
  
  @Produces @Current @Default @JobScoped
  public JobExecutionContext getJobExecutionContext() {
    return executionContext;
  }
  
  @Produces @Current @Default @JobScoped
  public JobDetail getJobDetail() {
    return executionContext.getJobDetail();
  }
  
  @Produces @Current @Default @JobScoped
  public JobDataMap getJobDataMap() {
    return executionContext.getJobDetail().getJobDataMap();
  }
  
  @End(JobScoped.class)
  public void execute(JobExecutionContext context) throws JobExecutionException {
    executionContext = context;
    String classname = context.getJobDetail().getJobDataMap().getString(JOB_CLASS_NAME_PROPERTY);
    try {
      Runnable job = getJob((Class<? extends Runnable>)Class.forName(classname));
      job.run();
    } catch (ClassNotFoundException e) {
      throw new JobExecutionException(e);
    }
  }
  
  private <T extends Runnable> T getJob(Class<T> type) {
    return jobInstance.select(type).get();
  }
}
