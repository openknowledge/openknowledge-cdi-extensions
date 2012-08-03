/*
 * Copyright open knowledge GmbH
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

package de.openknowledge.cdi.scope;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Arne Limburg - open knowledge GmbH (arne.limburg@openknowledge.de)
 */
@TestScope
public class TestScopedChild {

  private static AtomicInteger idCount = new AtomicInteger();

  private int id;

  public TestScopedChild() {
    this.id = idCount.incrementAndGet();
  }

  @Override
  public int hashCode() {
    return this.id;
  }

  public boolean equals(Object object) {
    if (!(object instanceof TestScopedChild)) {
      return false;
    }
    return hashCode() == ((TestScopedChild) object).hashCode();
  }
}
