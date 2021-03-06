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

import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.param.Validator;

/**
 * This Class is a copy of
 * {@link org.ocpsoft.rewrite.faces.annotation.handler.DeferredHandler} which
 * has been made non-public for some reasons
 *
 * @author oswald
 */
public class DeferredValidator implements Validator<Object> {

  private Validator<?> deferred;

  public DeferredValidator(Validator<?> validator) {
    this.deferred = validator;
  }

  @Override
  public boolean isValid(Rewrite event, EvaluationContext context, Object value) {
    return true;
  }

  public Validator<?> getDeferred() {
    return deferred;
  }
}
