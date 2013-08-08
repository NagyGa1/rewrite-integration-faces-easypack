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

import com.inswood.web.rewrite.faces.annotation.URLAction;
import com.inswood.web.rewrite.faces.util.ConditionalOperation;
import com.inswood.web.rewrite.faces.util.RewritePhaseConfig;
import java.lang.reflect.Method;
import org.ocpsoft.rewrite.annotation.api.MethodContext;
import org.ocpsoft.rewrite.config.Invoke;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.config.Operations;
import org.ocpsoft.rewrite.config.RuleBuilder;
import org.ocpsoft.rewrite.el.El;
import org.ocpsoft.rewrite.faces.annotation.config.IgnorePostbackOperation;
import org.ocpsoft.rewrite.faces.config.PhaseOperation;

/**
 * Implementation of {@link ClassMemberHandler}, converts {@link URLAction}
 * annotated class method into a Rewrite operation and appends it to the queue
 * of operations of provided {@link RuleBuilder}
 *
 * @author oswald
 */
public class ActionHandler extends AbstractClassMemberHandler {

  private PhaseOperation<?> operation;

  private ActionHandler(PhaseOperation<?> deferred) {
    this.operation = deferred;
  }

  @Override
  public void process(RuleBuilder ruleBuilder) {
    // Append the operation to the current execution queue
    ruleBuilder.perform(Operations.onInbound(operation));
  }

  public static ActionHandler create(MethodContext context, URLAction annotation) {
    // Define class and method to create binding
    Class beanClass = context.getClassContext().getJavaClass();
    Method method = context.getJavaMethod();

    // create EL bindding
    El binding = getElBinding(beanClass, method);

    // create an Operation
    Operation elOperation = new ConditionalOperation(Invoke.binding(binding), annotation);
    Operation operation = annotation.ingorePostback() ? new IgnorePostbackOperation(elOperation) : elOperation;

    // Make the operation deferred by default (this is necessary for JSF)
    PhaseOperation<?> deferred = PhaseOperation.enqueue(operation, 10);

    // Apply phase configuration and return
    return new ActionHandler(RewritePhaseConfig.from(annotation).apply(deferred));
  }
}
