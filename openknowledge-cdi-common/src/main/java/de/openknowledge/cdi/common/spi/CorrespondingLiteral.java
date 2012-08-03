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

package de.openknowledge.cdi.common.spi;

import de.openknowledge.cdi.common.qualifier.Corresponding;

import javax.enterprise.util.AnnotationLiteral;

/**
 * @author Arne Limburg - open knowledge GmbH
 */
public class CorrespondingLiteral extends AnnotationLiteral<Corresponding> implements Corresponding {

  private Class<?> value;

  public CorrespondingLiteral(Class<?> value) {
    this.value = value;
  }

  @Override
  public Class<?> value() {
    return value;
  }
}
