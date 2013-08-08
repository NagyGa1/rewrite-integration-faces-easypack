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

/**
 * {@link org.ocpsoft.rewrite.config.Rule} that creates a bi-directional rewrite
 * rule between an externally facing path and an internal server resource path.
 * Functionally this annotation is the same as
 * {@link org.ocpsoft.rewrite.annotation.Join} but has different
 * {@link org.ocpsoft.rewrite.annotation.spi.AnnotationHandler}
 *
 * @author oswald
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface URLJoin {

  /**
   * The external path to which the resource will be exposed.
   *
   * @return
   */
  String path();

  /**
   * The internal resource to be exposed.
   *
   * @return
   */
  String to();
}
