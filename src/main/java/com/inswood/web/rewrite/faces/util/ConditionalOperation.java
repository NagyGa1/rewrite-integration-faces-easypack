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

import com.inswood.web.rewrite.faces.annotation.URLAction;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.servlet.impl.BaseHttpRewrite;

/**
 * Wrapper class for Operation which instance gets always queued but executes
 * only if current ViewId matches a predefined set of views
 *
 * @author oswald
 */
public class ConditionalOperation implements Operation {

  private final static Logger logger = Logger.getLogger(ConditionalOperation.class.getName());
  private Operation wrapped;
  private Set<String> actionViews;

  /**
   *
   * @param wrapped
   * @param annotation
   */
  public ConditionalOperation(Operation wrapped, URLAction annotation) {
    this.wrapped = wrapped;
    this.actionViews = new HashSet<>(Arrays.asList(annotation.views()));
  }

  @Override
  public void perform(Rewrite event, EvaluationContext context) {
    if (actionViews.isEmpty()) {
      // No restrictions, delegate
      wrapped.perform(event, context);
    } else if (event instanceof BaseHttpRewrite) {
      // Inbound rewrite event
      HttpServletRequest request = ((BaseHttpRewrite) event).getRequest();
      String requestUri = request.getRequestURI();
      String contextPath = request.getServletContext().getContextPath();
      // Get the View Id
      String requestViewId = requestUri.substring(contextPath.length());
      // Perform if the viewId matches
      if (actionViews.contains(requestViewId)) {
        wrapped.perform(event, context);
      }
    } else {
      // No idea what to do, delegating :-)
      logger.log(Level.WARNING, "Non-inbound rewrite event for conditional ''@{0}''", URLAction.class.getSimpleName());
      wrapped.perform(event, context);
    }
  }
}
