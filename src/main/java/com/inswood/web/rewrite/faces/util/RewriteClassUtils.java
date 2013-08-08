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
package com.inswood.web.rewrite.faces.util;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import org.ocpsoft.rewrite.annotation.Convert;
import org.ocpsoft.rewrite.annotation.Validate;
import org.ocpsoft.rewrite.param.Converter;
import org.ocpsoft.rewrite.param.Validator;

/**
 * Useful methods
 *
 * @author oswald
 */
public class RewriteClassUtils {

  /**
   * Returns class hierarchy for a given class (excluding
   * {@link java.lang.Object}). The argument comes first in the resulting
   * {@link LinkedList}
   */
  public static LinkedList<Class> createClassHierarchy(Class clazz) {
    LinkedList<Class> superclasses = new LinkedList<>();
    Class c = clazz;
    while (!c.equals(Object.class)) {
      superclasses.add(c);
      c = c.getSuperclass();
    }
    return superclasses;
  }

  /**
   * Returns only those property names of the Class which have both setter and
   * getter in accordance with JavaBeans spec.
   */
  public static Set<String> getBeanClassProperties(Class clazz) {
    Set<String> props = new HashSet<>();
    try {
      // Get all the class properties
      PropertyDescriptor[] descriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
      // Loop and select only the ones with getter and setter
      for (PropertyDescriptor d : descriptors) {
        //TODO check the accessibility of methods
        if (d.getReadMethod() != null && d.getWriteMethod() != null) {
          props.add(d.getName());
        }
      }
      return props;
    } catch (Throwable t) {
      throw new RuntimeException("Unable to examine the class '" + clazz.getName() + "'", t);
    }
  }

  /**
   * Reads {@link Field} annotations and creates {@link Validator} if
   * {@link Validate} annotation is found, otherwise method returns null.
   */
  public static Validator getFieldValidator(Field field) {
    Validate validateAnnotation = field.getAnnotation(Validate.class);
    if (validateAnnotation != null) {
      // identify validator by the type of the validator
      if (validateAnnotation.with() != Object.class) {
        return LazyValidatorAdapter.forValidatorType(validateAnnotation.with());
      } // identify validator by some kind of unique id
      else if (validateAnnotation.id().length() > 0) {
        return LazyValidatorAdapter.forValidatorId(validateAnnotation.id());
      } // default: identify validator by the target type
      else {
        return LazyValidatorAdapter.forTargetType(field.getType());
      }
    } else {
      return null;
    }
  }

  /**
   * Reads {@link Field} annotations and creates {@link Converter} if
   * {@link Convert} annotation is found, otherwise method returns null.
   */
  public static Converter getFieldConverter(Field field) {
    Convert convertAnnotation = field.getAnnotation(Convert.class);
    if (convertAnnotation != null) {
      // identify converter by the type of the converter
      if (convertAnnotation.with() != Object.class) {
        return LazyConverterAdapter.forConverterType(convertAnnotation.with());
      } // identify converter by some kind of unique id
      else if (convertAnnotation.id().length() > 0) {
        return LazyConverterAdapter.forConverterId(convertAnnotation.id());
      } // default: identify converter by the target type
      else {
        return LazyConverterAdapter.forTargetType(field.getType());
      }
    } else {
      return null;
    }
  }
}
