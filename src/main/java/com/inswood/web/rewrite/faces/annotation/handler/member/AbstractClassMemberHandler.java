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
package com.inswood.web.rewrite.faces.annotation.handler.member;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Iterator;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.rewrite.el.El;
import org.ocpsoft.rewrite.el.spi.BeanNameResolver;

/**
 * Abstract implementation of {@link ClassMemberHandler} with a few utility
 * static methods
 *
 * @author oswald
 */
abstract class AbstractClassMemberHandler implements ClassMemberHandler {

  /**
   * Required when the declaring class of a class member differs from the bean
   * class (e.g. in a class hierarchy)
   */
  protected static El getElBinding(Class clazz, final Member member) {
    // Check inheritance
    if (!member.getDeclaringClass().isAssignableFrom(clazz)) {
      throw new RuntimeException(
              "The declaring class '" + member.getDeclaringClass().getName()
              + "' of the member '" + member.getName()
              + "' is not assignable from the bean class '" + clazz.getName() + "'");
    }

    // load available SPI implementations
    Iterator<BeanNameResolver> iterator = ServiceLoader.load(BeanNameResolver.class).iterator();
    while (iterator.hasNext()) {
      BeanNameResolver resolver = iterator.next();
      String beanName = resolver.getBeanName(clazz);
      if (beanName != null) {
        String el = new StringBuilder()
                .append(beanName).append('.')
                .append(member.getName())
                .append(member instanceof Method ? "()" : "")
                .toString();
        return El.ElProperty.property(el);
      }
    }
    throw new IllegalStateException("Unable to obtain EL name for bean of type [" + clazz.getName()
            + "] from any of the SPI implementations");
  }
}
