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

import java.util.Iterator;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Assert;
import org.ocpsoft.rewrite.annotation.handler.ConvertHandler;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Converter;
import org.ocpsoft.rewrite.spi.ConverterProvider;

/**
 * This source is a copy of inner private static class LazyConverterAdapter in
 * {@link org.ocpsoft.rewrite.annotation.handler.ConvertHandler}. IMO it should
 * be made public for reuse.
 */
public class LazyConverterAdapter implements Converter<Object> {

  private final Class<?> targetType;
  private final String converterId;
  private final Class<?> converterType;

  private LazyConverterAdapter(Class<?> targetType, String converterId, Class<?> converterType) {
    this.targetType = targetType;
    this.converterId = converterId;
    this.converterType = converterType;
  }

  public static LazyConverterAdapter forConverterType(Class<?> converterType) {
    return new LazyConverterAdapter(null, null, converterType);
  }

  public static LazyConverterAdapter forConverterId(String id) {
    return new LazyConverterAdapter(null, id, null);
  }

  public static LazyConverterAdapter forTargetType(Class<?> targetType) {
    return new LazyConverterAdapter(targetType, null, null);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Object convert(Rewrite event, EvaluationContext context, Object value) {

    Converter<?> converter = null;

    // let one of the SPI implementations build the converter
    Iterator<ConverterProvider> providers = ServiceLoader.load(ConverterProvider.class).iterator();
    while (providers.hasNext()) {
      ConverterProvider provider = providers.next();

      if (targetType != null) {
        converter = provider.getByTargetType(targetType);
      } else if (converterType != null) {
        converter = provider.getByConverterType(converterType);
      } else {
        converter = provider.getByConverterId(converterId);
      }

      if (converter != null) {
        break;
      }
    }
    Assert.notNull(converter, "Got no converter from any ConverterProvider for: " + this.toString());
    return converter.convert(event, context, value);
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    b.append(this.getClass().getSimpleName());
    b.append(" for ");
    if (targetType != null) {
      b.append(" target type ");
      b.append(targetType.getName());
    } else if (converterType != null) {
      b.append(" converter type ");
      b.append(converterType.getName());
    } else {
      b.append(" id ");
      b.append(converterId);
    }
    return b.toString();
  }
}
