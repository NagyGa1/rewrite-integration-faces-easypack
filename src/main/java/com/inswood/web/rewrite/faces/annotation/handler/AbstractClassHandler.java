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

import com.inswood.web.rewrite.faces.annotation.URLAction;
import com.inswood.web.rewrite.faces.annotation.URLParameter;
import com.inswood.web.rewrite.faces.annotation.handler.member.ActionHandler;
import com.inswood.web.rewrite.faces.annotation.handler.member.ClassMemberHandler;
import com.inswood.web.rewrite.faces.annotation.handler.member.ParameterHandler;
import com.inswood.web.rewrite.faces.util.RewriteClassUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.ocpsoft.rewrite.annotation.RequestAction;
import org.ocpsoft.rewrite.annotation.api.ClassContext;
import org.ocpsoft.rewrite.annotation.api.HandlerChain;
import org.ocpsoft.rewrite.annotation.context.FieldContextImpl;
import org.ocpsoft.rewrite.annotation.context.MethodContextImpl;
import org.ocpsoft.rewrite.annotation.handler.HandlerWeights;
import org.ocpsoft.rewrite.annotation.spi.AnnotationHandler;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.faces.annotation.Deferred;
import org.ocpsoft.rewrite.faces.annotation.IgnorePostback;

public abstract class AbstractClassHandler<A extends Annotation> implements AnnotationHandler<A> {

  private static final Logger logger = Logger.getLogger(AbstractClassHandler.class.getName());
  /**
   * The use of following annotations along with {@link URLAction} is obsolete,
   * confusing and discouraged
   */
  private static final Class<? extends Annotation>[] CONFLICTING_METHOD_ANNOTATIONS = new Class[]{
    RequestAction.class,
    Deferred.class,
    IgnorePostback.class
  };
  /**
   * The use of following annotations along with {@link URLParameter} is
   * obsolete, confusing and discouraged
   */
  private static final Class<? extends Annotation>[] CONFLICTING_FIELD_ANNOTATIONS = new Class[]{
    Parameter.class,
    Deferred.class,
    IgnorePostback.class
  };

  /**
   * An implementation must provide list of rules associated with a bean class
   */
  protected abstract List<Rule> generateClassRules(Class beanClass, A annotation);

  /**
   * An implementation must provide the list of class annotations which use is
   * not allowed along with the annotation being handled.
   */
  protected abstract Class<? extends Annotation>[] getConflictingClassAnnotations();

  @Override
  public int priority() {
    return HandlerWeights.WEIGHT_TYPE_STRUCTURAL;
  }

  /**
   * Main method of
   * {@link org.ocpsoft.rewrite.annotation.spi.AnnotationHandler}. To support
   * method and property inheritance this library does not rely on type- or
   * method-level annotation handlers, only type-level annotation handlers are
   * used. Thus, all the {@link URLAction} and {@link URLParameter} annotations
   * need to be scanned during the processing of the bean class, from the bean
   * class itself up to it's topmost super-class
   */
  @Override
  public void process(ClassContext context, A annotation, HandlerChain chain) {
    Class beanClass = context.getJavaClass();
    assertClassAnnotations(beanClass, annotation);
    // Get rules for this particular bean class, there maybe several ones if multiple joins are used.
    List<Rule> rules = generateClassRules(beanClass, annotation);
    // Prepare a queue of class member handlers (actions and bindings)
    // which will be applied to the each of the rules above
    List<ClassMemberHandler> handlerQueue = new LinkedList<>();
    // Scan the bean class and its superclasses to create and enqueue parameter handlers
    enqueueParameterHandlers(context, annotation, handlerQueue);
    // Scan the bean class and its superclasses to create and enqueue method handlers
    enqueueMethodHandlers(context, handlerQueue);

    for (Rule rule : rules) {
      // Start new rule
      context.setBaseRule(rule);
      // Apply handlers - bindings and operations
      for (ClassMemberHandler handler : handlerQueue) {
        handler.process(context.getRuleBuilder());
      }
    }
    // Proceed to the next class in chain
    logger.log(Level.INFO, "Bean class ''{0}'' has been processed.", beanClass.getName());
    chain.proceed();
  }

  private void enqueueMethodHandlers(ClassContext context, List<ClassMemberHandler> handlers) {
    Class beanClass = context.getJavaClass();
    // Scan and get public methods of the bean class, those could include inherited ones
    for (Method method : beanClass.getMethods()) {
      URLAction ann = method.getAnnotation(URLAction.class);
      if (ann != null) {
        assertMethodAnnotations(beanClass, method, ann);
        // Check the method signature
        if (method.getParameterTypes().length == 0 && method.getReturnType().equals(void.class)) {
          // Create and enqueue method handler
          MethodContextImpl methodContext = new MethodContextImpl(context, method);
          handlers.add(ActionHandler.create(methodContext, ann));
        } else {
          // Method is annotated but has wrong signature
          String msg = String.format(
                  "Method '%s' of the class '%s' has wrong signature. Bean class: '%s'",
                  method.getName(),
                  method.getDeclaringClass().getName(),
                  beanClass.getName());
          throw new IllegalStateException(msg);
        }
      }
    }
  }

  private void enqueueParameterHandlers(ClassContext context, A annotation, List<ClassMemberHandler> handlers) {
    Class beanClass = context.getJavaClass();
    // Get class hierarchy
    LinkedList<Class> classHierarchy = RewriteClassUtils.createClassHierarchy(beanClass);
    // Get a representation of the class as a JavaBean
    Set<String> beanProperties = RewriteClassUtils.getBeanClassProperties(beanClass);
    // Handle field annotations
    for (Class clazz : classHierarchy) {
      for (Field field : clazz.getDeclaredFields()) {
        URLParameter ann = field.getAnnotation(URLParameter.class);
        if (ann != null) {
          assertFieldAnnotations(beanClass, field, ann);
          if (beanProperties.contains(field.getName())) {
            // Field has the annotation and getter/setter
            // Create and enqueue parameter handler
            FieldContextImpl fieldContext = new FieldContextImpl(context, field);
            handlers.add(ParameterHandler.create(fieldContext, ann));
          } else {
            // Field has the annotation but no getter/setter
            String msg = String.format(
                    "Field '%s' of the class '%s' is annotated by '@%s' but has no getter and/or setter (bean class '%s')",
                    field.getName(),
                    field.getDeclaringClass().getName(),
                    URLParameter.class.getSimpleName(),
                    beanClass.getName());
            throw new RuntimeException(msg);
          }
        }
      }
    }
  }

  private void assertClassAnnotations(Class clazz, Annotation classAnnotation) {
    Class[] conflictAnnotations = getConflictingClassAnnotations() == null ? new Class[]{} : getConflictingClassAnnotations();
    for (Class c : conflictAnnotations) {
      Annotation ann = clazz.getAnnotation(c);
      if (ann != null) {
        String msg = String.format(
                "Annotation '@%s' is not allowed when '@%s' is used. Class: '%s'",
                ann.annotationType().getName(),
                classAnnotation.annotationType().getName(),
                clazz.getName());
        throw new IllegalStateException(msg);
      }
    }
  }

  private void assertMethodAnnotations(Class beanClass, Method method, Annotation methodAnnotation) {
    for (Class c : CONFLICTING_METHOD_ANNOTATIONS) {
      Annotation ann = method.getAnnotation(c);
      if (ann != null) {
        String msg = String.format(
                "Annotation '@%s' is not allowed when '@%s' is used. Class: '%s', Method: '%s', Bean class: '%s'",
                ann.annotationType().getName(),
                methodAnnotation.annotationType().getName(),
                method.getDeclaringClass().getName(),
                method.getName(),
                beanClass.getName());
        throw new IllegalStateException(msg);
      }
    }
  }

  private void assertFieldAnnotations(Class beanClass, Field field, Annotation fieldAnnotation) {
    for (Class c : CONFLICTING_FIELD_ANNOTATIONS) {
      Annotation ann = field.getAnnotation(c);
      if (ann != null) {
        String msg = String.format(
                "Annotation '@%s' is not allowed when '@%s' is used. Class: '%s', Field: '%s', Bean class: '%s'",
                ann.annotationType().getName(),
                fieldAnnotation.annotationType().getName(),
                field.getDeclaringClass().getName(),
                field.getName(),
                beanClass.getName());
        throw new IllegalStateException(msg);
      }
    }
  }
}
