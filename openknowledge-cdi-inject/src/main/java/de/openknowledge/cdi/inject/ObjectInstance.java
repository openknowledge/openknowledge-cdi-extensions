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

import java.lang.annotation.Annotation;
import java.util.Iterator;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.TypeLiteral;
import javax.inject.Inject;

/**
 * This class may be instantiated via the default-constructor
 * and used to retrieve CDI beans in a non-managed environment.
 *
 * @author Arne Limburg - open knowledge GmbH (arne.limburg@openknowledge.de)
 */
public class ObjectInstance implements Instance<Object> {

  @Inject
  @Any
  private Instance<Object> delegate;

  public ObjectInstance() {
    new CdiInjector<ObjectInstance>().inject(this);
  }

  public Object get() {
    return delegate.get();
  }

  public boolean isAmbiguous() {
    return delegate.isAmbiguous();
  }

  public boolean isUnsatisfied() {
    return delegate.isUnsatisfied();
  }

  public Iterator<Object> iterator() {
    return delegate.iterator();
  }

  public Instance<Object> select(Annotation... arg0) {
    return delegate.select(arg0);
  }

  public <U> Instance<U> select(Class<U> arg0, Annotation... arg1) {
    return delegate.select(arg0, arg1);
  }

  public <U> Instance<U> select(TypeLiteral<U> arg0, Annotation... arg1) {
    return delegate.select(arg0, arg1);
  }
}
