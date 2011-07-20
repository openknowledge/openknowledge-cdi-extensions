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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import javax.inject.Inject;

import de.openknowledge.cdi.test.CdiJunit4TestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

/**
 * @author Arne Limburg - open knowledge GmbH
 */
@RunWith(CdiJunit4TestRunner.class)
public class JobTest {

  @Inject
  private Scheduler scheduler;
  @Inject
  private TestJob testJob;

  @Test(timeout = 10000)
  public void scheduleJob() throws SchedulerException {
    JobDetail job = newJob(TestJob.class).build();
    Trigger trigger = newTrigger().startNow().build();
    assertFalse(testJob.isFinished());
    scheduler.scheduleJob(job, trigger);
    testJob.setStarted();
    assertTrue(testJob.isFinished());
  }
}
