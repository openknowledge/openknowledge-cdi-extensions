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

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;

/**
 * This implementation can be used to modify the scanned annotations of a CDI bean during annotation-processing
 * in a CDI extension (i.e. add annotations). See {@link DelegatingAnnotatedType} for a detailed example.
 * <p/>
 * If you want to modify annotations of parameters of this {@link AnnotatedCallable}
 * you may implement your own {@link AnnotatedCallable}, inherit from {@link DelegatingAnnotatedCallable}
 * and override {@link DelegatingAnnotatedCallable#processAnnotatedParameter(AnnotatedParameter)}
 * to return an instance of {@link DelegatingAnnotatedCallable}.
 * Example:
 * <pre>
 * public class MyAnnotatedCallable<T> extends DelegatingAnnotatedCallable<T, Method> implements AnnotatedMethod<T> {
 *
 *   private Annotation[] additionalAnnotations;
 *
 *   public MyAnnotatedCallable(AnnotatedType<T> delegate, Annotation... additionalAnnotations) {
 *     super(delegate, additionalAnnotations);
 *     this.additionalAnnotations = additionalAnnotations;
 *   }
 *
 *   protected AnnotatedParameter<T> processAnnotatedParameter(AnnotatedParameter<T> parameter) {
 *     return new DelegatingAnnotatedParameter<T>(this, parameter, this.additionalAnnotations);
 *   }
 * }
 * </pre>
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public class DelegatingAnnotatedCallable<T, M extends Member> extends DelegatingAnnotatedMember<T, M>
  implements AnnotatedCallable<T> {

  private AnnotatedCallable<T> delegate;

  public DelegatingAnnotatedCallable(AnnotatedType<T> declaringType,
                                     AnnotatedCallable<T> delegateCallable,
                                     Annotation... additionalAnnotations) {
    super(declaringType, delegateCallable, additionalAnnotations);
    delegate = delegateCallable;
  }

  @Override
  public List<AnnotatedParameter<T>> getParameters() {
    List<AnnotatedParameter<T>> parameters = new ArrayList<AnnotatedParameter<T>>();
    for (AnnotatedParameter<T> annotatedParameter : delegate.getParameters()) {
      parameters.add(processAnnotatedParameter(annotatedParameter));
    }
    return parameters;
  }

  protected AnnotatedParameter<T> processAnnotatedParameter(AnnotatedParameter<T> annotatedParameter) {
    return annotatedParameter;
  }
}
