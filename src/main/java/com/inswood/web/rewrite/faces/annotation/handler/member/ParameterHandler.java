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

import com.inswood.web.rewrite.faces.annotation.URLParameter;
import com.inswood.web.rewrite.faces.util.RewriteClassUtils;
import com.inswood.web.rewrite.faces.util.DeferredConverter;
import com.inswood.web.rewrite.faces.util.DeferredValidator;
import com.inswood.web.rewrite.faces.util.RewritePhaseConfig;
import java.lang.reflect.Field;
import org.ocpsoft.rewrite.annotation.api.FieldContext;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.config.Condition;
import org.ocpsoft.rewrite.config.ConditionBuilder;
import org.ocpsoft.rewrite.config.Conditions;
import org.ocpsoft.rewrite.config.Or;
import org.ocpsoft.rewrite.config.RuleBuilder;
import org.ocpsoft.rewrite.config.True;
import org.ocpsoft.rewrite.faces.annotation.config.IgnorePostbackBinding;
import org.ocpsoft.rewrite.faces.config.PhaseBinding;
import org.ocpsoft.rewrite.param.ConfigurableParameter;
import org.ocpsoft.rewrite.param.Converter;
import org.ocpsoft.rewrite.param.Validator;
import org.ocpsoft.rewrite.servlet.config.RequestParameter;

/**
 * Implementation of {@link ClassMemberHandler}, converts {@link URLParameter}
 * annotated class field into a Rewrite rule conditions and bindings.
 *
 * @author oswald
 */
public class ParameterHandler extends AbstractClassMemberHandler {

  private String param;
  private Condition condition;
  private Binding elBinding;
  private RewritePhaseConfig phaseConfig;
  private Converter converter;
  private Validator validator;

  private ParameterHandler(String param, Condition condition, Binding elBinding, RewritePhaseConfig phaseConfig, Converter converter, Validator validator) {
    this.param = param;
    this.condition = condition;
    this.elBinding = elBinding;
    this.phaseConfig = phaseConfig;
    this.converter = converter;
    this.validator = validator;
  }

  @Override
  public void process(RuleBuilder ruleBuilder) {
    ConfigurableParameter<?> parameterBuilder = ruleBuilder.when(condition).where(param);
    PhaseBinding phaseBinding = PhaseBinding.to(elBinding);

    // configure converter
    if (converter != null) {
      Converter c = converter instanceof DeferredConverter
              ? ((DeferredConverter) converter).getDeferred() : converter;

      parameterBuilder.convertedBy(c);
      phaseBinding.convertedBy(new DeferredConverter(c));
    }

    // configure validator
    if (validator != null) {
      Validator v = validator instanceof DeferredValidator
              ? ((DeferredValidator) validator).getDeferred() : validator;

      parameterBuilder.validatedBy(v);
      phaseBinding.validatedBy(new DeferredValidator(v));
    }

    // configure the target phase and return
    parameterBuilder.bindsTo(phaseConfig.apply(phaseBinding));
  }

  // Factory.
  public static ParameterHandler create(FieldContext context, URLParameter annotation) {
    Field field = context.getJavaField();
    // Define parameter name
    String param = annotation.value().isEmpty()
            ? context.getJavaField().getName() : annotation.value().trim();

    // Create condition
    ConditionBuilder conditionBuilder = Conditions.create();
    Condition requestParameter = RequestParameter.matches(param, "{" + param + "}");
    Condition condition = conditionBuilder.and(Or.any(requestParameter, new True()));

    // Create EL binding
    Binding binding = getElBinding(context.getClassContext().getJavaClass(), field);
    binding = annotation.ingorePostback() ? new IgnorePostbackBinding(binding) : binding;

    // Create the Converter if necessary
    Converter<?> converter = RewriteClassUtils.getFieldConverter(field);
    // Create the Validator
    Validator<?> validator = RewriteClassUtils.getFieldValidator(field);
    // read phase configuration and return new handler
    RewritePhaseConfig phaseConfig = RewritePhaseConfig.from(annotation);

    return new ParameterHandler(
            param,
            condition,
            binding,
            phaseConfig,
            converter,
            validator);
  }
}
