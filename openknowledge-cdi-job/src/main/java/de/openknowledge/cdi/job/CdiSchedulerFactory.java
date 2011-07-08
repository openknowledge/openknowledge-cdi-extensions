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
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.core.QuartzScheduler;
import org.quartz.core.QuartzSchedulerResources;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;

/**
 * @author Arne Limburg - open knowledge GmbH
 */
@ApplicationScoped
public class CdiSchedulerFactory extends StdSchedulerFactory {

  @Inject
  private JobFactory jobFactory;

  @Produces
  @ApplicationScoped
  public Scheduler createScheduler() {
    try {
      Scheduler scheduler = super.getScheduler();
      scheduler.start();
      return scheduler;
    } catch (SchedulerException e) {
      throw new IllegalArgumentException(e);
    }
  }

  protected Scheduler instantiate(QuartzSchedulerResources resources, QuartzScheduler scheduler) {
    try {
      scheduler.setJobFactory(jobFactory);
      return super.instantiate(resources, scheduler);
    } catch (SchedulerException e) {
      throw new IllegalStateException(e);
    }
  }
}
