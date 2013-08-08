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
import com.inswood.web.rewrite.faces.annotation.URLParameter;
import javax.faces.event.PhaseId;
import org.ocpsoft.rewrite.faces.annotation.Phase;
import org.ocpsoft.rewrite.faces.config.PhaseBinding;
import org.ocpsoft.rewrite.faces.config.PhaseOperation;

/**
 * Convenience class to store and apply Faces Phase configuration to actions and
 * bindings
 *
 * @author oswald
 */
public class RewritePhaseConfig {

  private boolean after;
  private PhaseId phaseId;

  private RewritePhaseConfig(boolean after, PhaseId phaseId) {
    this.after = after;
    this.phaseId = phaseId;
  }

  /**
   *
   * @param binding
   * @return
   */
  public PhaseBinding apply(PhaseBinding binding) {
    if (after) {
      return binding.after(phaseId);
    } else {
      return binding.before(phaseId);
    }
  }

  /**
   *
   * @param operation
   * @return
   */
  public PhaseOperation apply(PhaseOperation operation) {
    if (after) {
      return operation.after(phaseId);
    } else {
      return operation.before(phaseId);
    }
  }

  /**
   *
   * @param annotation
   * @return
   */
  public static RewritePhaseConfig from(URLParameter annotation) {
    return factory(annotation.before(), annotation.after());
  }

  /**
   *
   * @param annotation
   * @return
   */
  public static RewritePhaseConfig from(URLAction annotation) {
    return factory(annotation.before(), annotation.after());
  }

  private static RewritePhaseConfig factory(Phase before, Phase after) {
    if (before == Phase.NONE && after == Phase.NONE) {
      return new RewritePhaseConfig(true, PhaseId.RESTORE_VIEW);
    } else if (before != Phase.NONE && after == Phase.NONE) {
      return new RewritePhaseConfig(false, before.getPhaseId());
    } else if (before == Phase.NONE && after != Phase.NONE) {
      return new RewritePhaseConfig(true, after.getPhaseId());
    } else {
      throw new IllegalStateException("You cannot use after() and before() at the same time.");
    }
  }
}
