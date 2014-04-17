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

import com.inswood.web.rewrite.faces.annotation.URLJoin;
import com.inswood.web.rewrite.faces.annotation.URLJoins;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.servlet.config.rule.Join;

/**
 * Implementation of {@link AbstractClassHandler} for processing bean class
 * annotated by {@link URLJoins}
 *
 * @author oswald
 */
public class URLJoinsHandler extends AbstractClassHandler<URLJoins> {

  private static final Class[] CONFLICTING_CLASS_ANNOTATIONS = new Class[]{
    URLJoin.class,
    org.ocpsoft.rewrite.annotation.Join.class,
    org.ocpsoft.rewrite.annotation.Rule.class
  };

  @Override
  public Class<URLJoins> handles() {
    return URLJoins.class;
  }

  /**
   * The use of the following annotations is not allowed when the bean is
   * annotated as {@link URLJoins}
   */
  @Override
  protected Class<? extends Annotation>[] getConflictingClassAnnotations() {
    return CONFLICTING_CLASS_ANNOTATIONS;
  }

  /**
   * Method returns list of several {@link Rule}, one per each child
   * {@link URLJoin} annotation
   */
  @Override
  protected List<Rule> generateClassRules(Class beanClass, URLJoins annotation) {
    Map<String, String> mappings = getMappings(beanClass, annotation);
    List<Rule> rules = new ArrayList<>();
    for (String path : mappings.keySet()) {
      rules.add(Join.path(path).to(mappings.get(path)));
    }
    return rules;
  }

  /**
   * Method checks duplicated entries and returns the resulting path->view map
   */
  private Map<String, String> getMappings(Class clazz, URLJoins annotation) {
    Map<String, String> mappings = new HashMap<>();
    HashSet<String> visitedViews = new HashSet<>();
    HashSet<String> visitedPaths = new HashSet<>();
    for (URLJoin join : annotation.joins()) {
      String path = join.path();
      String view = join.to();

    // TODO add ambiguity checks, not just comparing strings
	/*
	 * This was a further restriction over Rewrite, it kills this
	 * business case that was accepted: <pre> <code>
	 * 
	 * @URLJoins(joins = {
	 * 
	 * @URLJoin(path = "/b2c/quote/lieva/", to = "/gh/b2c/quote.xhtml"),
	 * 
	 * @URLJoin( path = "/b2c/quote/lieva/{quoteNo}/{verifyToken}", to =
	 * "/gh/b2c/quote.xhtml") }) </code></pre>
	 * 
	 * Okay, this is not bi-directional.
	 */
	// if (visitedViews.contains(view)) {
	// throw new IllegalStateException("Duplicate viewId '" + view
	// + "' in @" + URLJoins.class.getSimpleName()
	// + " annotation for class '" + clazz.getName() + "'");
	// }
      
      if (visitedPaths.contains(path)) {
        throw new IllegalStateException("Duplicate path '" + path + "' in @" + URLJoins.class.getSimpleName() + " annotation for class '" + clazz.getName() + "'");
      }
      visitedViews.add(view);
      visitedPaths.add(path);
      mappings.put(path, view);
    }

    return mappings;
  }
}
