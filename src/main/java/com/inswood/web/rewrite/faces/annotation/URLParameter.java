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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.faces.annotation.Phase;

/**
 * Registers a {@link org.ocpsoft.rewrite.bind.Binding} for a parameter. This
 * annotation can be used to bind a parameter to a bean property. This
 * annotation is a all-in-one replacement for
 * {@link org.ocpsoft.rewrite.annotation.Parameter}, {@link org.ocpsoft.rewrite.faces.annotation.Deferred}
 * and {@link org.ocpsoft.rewrite.faces.annotation.IgnorePostback} and is more
 * convenient in JSF environment. Unlike
 * {@link org.ocpsoft.rewrite.annotation.Parameter}, the annotated url parameter
 * gets inherited from bean's super-classes.
 *
 * @author oswald
 */
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface URLParameter {

  /**
   * The name of the parameter. If the attribute is not set, the name of the
   * annotated field is used.
   *
   * @return
   */
  String value() default "";

  /**
   * The {@link Phase} before which the binding should be processed
   *
   * @return
   */
  Phase before() default Phase.NONE;

  /**
   * The {@link Phase} after which the binding should be processed.
   *
   * @return
   */
  Phase after() default Phase.NONE;

  /**
   * Specifies whether an element annotated with {@literal @}URLParameter should
   * be executed if the current {@link HttpServletRequest} is handling a
   * JavaServer Faces form postback.
   *
   * @return
   */
  boolean ingorePostback() default false;
}
