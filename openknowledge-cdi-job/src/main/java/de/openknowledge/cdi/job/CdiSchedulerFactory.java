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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import de.openknowledge.cdi.common.property.Property;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.core.QuartzScheduler;
import org.quartz.core.QuartzSchedulerResources;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.JobFactory;

import java.util.Properties;

/**
 * Our cdi enabled scheduler factory. Pretty much works like Quartz
 * StdSchedulerFactory, however properties may be provided through cdi
 * {@link de.openknowledge.cdi.common.property.Property} support. If you
 * use in a {@link de.openknowledge.cdi.common.property.Property} enabled
 * environment regular org.quartz properties they will be injected to this
 * factory.
 *
 * <p/>
 * If CDI does not provide properties standard quartz configuration
 * applies.
 *
 * @author Arne Limburg - open knowledge GmbH
 */
@ApplicationScoped
public class CdiSchedulerFactory extends StdSchedulerFactory {

  @Inject
  private JobFactory jobFactory;

  @Inject
  @Property(name = "org.quartz.*", mask = true)
  private Properties properties;

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

  public void destroyScheduler(@Disposes Scheduler aScheduler) {
    try {
      aScheduler.shutdown(true);
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

  @PostConstruct
  protected void init() {
    if (properties!= null && properties.size() > 0) {
      try {
        initialize(properties);
      } catch (SchedulerException e) {
        throw new IllegalArgumentException(e);
      }
    }

  }
}
