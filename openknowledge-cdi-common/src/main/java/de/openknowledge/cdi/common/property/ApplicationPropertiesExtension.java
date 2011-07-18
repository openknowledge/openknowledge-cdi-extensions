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

package de.openknowledge.cdi.common.property;

import de.openknowledge.cdi.common.qualifier.Current;
import de.openknowledge.cdi.common.spi.SingletonBean;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.util.AnnotationLiteral;
import java.util.List;
import java.util.Set;

/**
 * Injects an instance of {@link Current} {@link ApplicationProperties} containing
 * the supported {@link Property} instances of an application.
 *
 * @author Jens Schumann - open knowledge GmbH
 * @version $Revision: 7682 $
 */
public class ApplicationPropertiesExtension implements Extension {

  private ApplicationProperties properties = new ApplicationProperties();
  
  public void recordProperty(@Observes ProcessAnnotatedType event) {

    AnnotatedType annotatedType = event.getAnnotatedType();
    Set<AnnotatedField> fields = annotatedType.getFields();
    for (AnnotatedField f : fields) {
      if (f.isAnnotationPresent(Property.class)) {
        Property annotation = f.getAnnotation(Property.class);
        properties.addProperty((annotation));
      }
    }

    Set<AnnotatedMethod> methods = annotatedType.getMethods();
    for (AnnotatedMethod method : methods) {
      List<AnnotatedParameter> params = method.getParameters();
      parseParameters(params);
    }

    Set<AnnotatedConstructor> constructors = annotatedType.getConstructors();
    for (AnnotatedConstructor constructor : constructors) {
      List<AnnotatedParameter> params = constructor.getParameters();
      parseParameters(params);
    }
  }

  public void publishApplicationProperties(@Observes AfterBeanDiscovery event, BeanManager bm) {
    event.addBean(new SingletonBean("__ext_applicationProperties",
      properties,
      bm,
      new AnnotationLiteral<Current>() {
      }));
  }

  private void parseParameters(List<AnnotatedParameter> aParams) {
    for (AnnotatedParameter param : aParams) {
      if (param.isAnnotationPresent(Property.class)) {
        properties.addProperty(param.getAnnotation(Property.class));
      }
    }
  }
}