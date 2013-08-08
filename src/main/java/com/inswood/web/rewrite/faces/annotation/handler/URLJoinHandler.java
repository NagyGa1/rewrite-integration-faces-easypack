/*
 * Copyright 2013 <a href="mailto:andrey.bichkevskiy@gmail.com">Andrey Bichkevskiy</a>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.inswood.web.rewrite.faces.annotation.handler;

import com.inswood.web.rewrite.faces.annotation.URLJoin;
import com.inswood.web.rewrite.faces.annotation.URLJoins;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.servlet.config.rule.Join;

/**
 * Implementation of {@link AbstractClassHandler} for processing bean class
 * annotated by {@link URLJoin}
 *
 * @author oswald
 */
public class URLJoinHandler extends AbstractClassHandler<URLJoin> {

  private static final Class[] CONFLICTING_CLASS_ANNOTATIONS = new Class[]{
    URLJoins.class,
    org.ocpsoft.rewrite.annotation.Join.class,
    org.ocpsoft.rewrite.annotation.Rule.class
  };

  @Override
  public Class<URLJoin> handles() {
    return URLJoin.class;
  }

  /**
   * The use of the following annotations is not allowed when the bean is
   * annotated as {@link URLJoin}
   */
  @Override
  protected Class<? extends Annotation>[] getConflictingClassAnnotations() {
    return CONFLICTING_CLASS_ANNOTATIONS;
  }

  /**
   * Method returns the list of one {@link Rule}, according to the
   * {@link URLJoin} annotation
   */
  @Override
  protected List<Rule> generateClassRules(Class beanClass, URLJoin annotation) {
    Rule rule = Join.path(annotation.path()).to(annotation.to());
    return Arrays.asList(rule);
  }
}
