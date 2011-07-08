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

package de.openknowledge.cdi.common.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedType;

/**
 * An implementation of {@link AnnotatedMember} that delegates and the superclass of various implementations
 * of the Annotated* type hierarchy.
 *
 * @author Arne Limburg - open knowledge GmbH
 */
public class DelegatingAnnotatedMember<T, M extends Member> extends DelegatingAnnotated implements AnnotatedMember<T> {

  private AnnotatedType<T> declaringType;
  private AnnotatedMember<T> delegate;

  public DelegatingAnnotatedMember(AnnotatedType<T> declaringAnnotatedType,
                                   AnnotatedMember<T> delegateMember,
                                   Annotation... additionalAnnotations) {
    super(delegateMember, additionalAnnotations);
    declaringType = declaringAnnotatedType;
    delegate = delegateMember;
  }

  @Override
  @SuppressWarnings("unchecked")
  public M getJavaMember() {
    return (M) delegate.getJavaMember();
  }

  @Override
  public boolean isStatic() {
    return delegate.isStatic();
  }

  @Override
  public AnnotatedType<T> getDeclaringType() {
    return declaringType;
  }
}
