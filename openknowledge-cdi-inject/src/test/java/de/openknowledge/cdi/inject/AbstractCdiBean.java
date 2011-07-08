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

package de.openknowledge.cdi.inject;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public abstract class AbstractCdiBean<T> {

  private Object marker = new Object();
  private boolean postConstructCalled = false;
  private boolean preDestroyCalled = false;

  public AbstractCdiBean() {
    super();
  }

  @PostConstruct
  public void setPostConstructed() {
    postConstructCalled = true;
  }

  @PreDestroy
  public void setPreDestroyed() {
    preDestroyCalled = true;
  }

  public boolean wasPostConstructCalled() {
    return postConstructCalled;
  }

  public boolean wasPreDestroyCalled() {
    return preDestroyCalled;
  }

  protected Object getMarker() {
    return this.marker;
  }

  public boolean equals(Object object) {
    if (!(object instanceof AbstractCdiBean)) {
      return false;
    }
    AbstractCdiBean cdiBean = (AbstractCdiBean) object;
    if (isProxy(cdiBean)) {
      return cdiBean.equals(this);
    }
    return this == object;
  }

  private boolean isProxy(AbstractCdiBean cdiBean) {
    return !cdiBean.marker.equals(cdiBean.getMarker());
  }
}