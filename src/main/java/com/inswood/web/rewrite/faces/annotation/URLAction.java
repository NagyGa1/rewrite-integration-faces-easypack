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
package com.inswood.web.rewrite.faces.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.ocpsoft.rewrite.faces.annotation.Phase;

/**
 * Adds an {@link Invoke} to the current rule. This annotation is a all-in-one
 * replacement for
 * {@link org.ocpsoft.rewrite.annotation.RequestAction}, {@link org.ocpsoft.rewrite.faces.annotation.Deferred}
 * and {@link org.ocpsoft.rewrite.faces.annotation.IgnorePostback} and is more
 * convenient in JSF environment. Unlike
 * {@link org.ocpsoft.rewrite.annotation.RequestAction}, the annotated method
 * gets inherited from bean's super-classes.
 *
 * @author oswald
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface URLAction {

  /**
   * The {@link Phase} before which the action should execute.
   *
   * @return
   */
  Phase before() default Phase.NONE;

  /**
   * The {@link Phase} after which the action should execute.
   *
   * @return
   */
  Phase after() default Phase.NONE;

  /**
   * Specifies that a method annotated with {@literal @}URLAction should not be
   * executed if the current {@link HttpServletRequest} is handling a JavaServer
   * Faces form postback.
   *
   * @return
   */
  boolean ingorePostback() default false;

  /**
   * The list of viewIds for which the action gets executed. Empty list means
   * the action executes for each viewId specified in bean`s mapping rules, e.g.
   * when {@link com.inswood.rewrite.faces.annotation.URLJoins} is used
   *
   * @return
   */
  String[] views() default {};
}
