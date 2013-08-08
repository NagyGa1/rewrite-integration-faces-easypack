/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inswood.web.rewrite.faces.util;

import java.util.Iterator;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Validator;
import org.ocpsoft.rewrite.spi.ValidatorProvider;

/**
 * This source is a copy of inner private static class LazyValidatorAdapter in
 * {@link org.ocpsoft.rewrite.annotation.handler.ValidateHandler}. IMO it should
 * be made public for reuse.
 */
public class LazyValidatorAdapter implements Validator<Object> {

  private final Class<?> targetType;
  private final String validatorId;
  private final Class<?> validatorType;

  private LazyValidatorAdapter(Class<?> targetType, String validatorId, Class<?> validatorType) {
    this.targetType = targetType;
    this.validatorId = validatorId;
    this.validatorType = validatorType;
  }

  public static LazyValidatorAdapter forValidatorType(Class<?> validatorType) {
    return new LazyValidatorAdapter(null, null, validatorType);
  }

  public static LazyValidatorAdapter forValidatorId(String id) {
    return new LazyValidatorAdapter(null, id, null);
  }

  public static LazyValidatorAdapter forTargetType(Class<?> targetType) {
    return new LazyValidatorAdapter(targetType, null, null);
  }

  @Override
  @SuppressWarnings({"rawtypes", "unchecked"})
  public boolean isValid(Rewrite event, EvaluationContext context, Object value) {

    Validator validator = null;

    // let one of the SPI implementations build the validator
    Iterator<ValidatorProvider> providers = ServiceLoader.load(ValidatorProvider.class).iterator();
    while (providers.hasNext()) {
      ValidatorProvider provider = providers.next();

      if (targetType != null) {
        validator = provider.getByTargetType(targetType);
      } else if (validatorType != null) {
        validator = provider.getByValidatorType(validatorType);
      } else {
        validator = provider.getByValidatorId(validatorId);
      }

      if (validator != null) {
        break;
      }

    }
    Assert.notNull(validator, "Got no validator from any ValidatorProvider for: " + this.toString());

    return validator.isValid(event, context, value);

  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append(this.getClass().getSimpleName());
    b.append(" for ");
    if (targetType != null) {
      b.append(" target type ");
      b.append(targetType.getName());
    } else if (validatorType != null) {
      b.append(" validator type ");
      b.append(validatorType.getName());
    } else {
      b.append(" id ");
      b.append(validatorId);
    }
    return b.toString();
  }
}
