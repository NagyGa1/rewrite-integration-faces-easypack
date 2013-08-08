package com.inswood.web.rewrite.faces.annotation.handler.member;

import org.ocpsoft.rewrite.config.RuleBuilder;

/**
 * Created per each annotated bean class member (method and/or field, URL-action
 * or URL-parameter) and provided with {@link RuleBuilder} to register
 * corresponding Rule actions, conditions and bindings
 */
public interface ClassMemberHandler {

  public void process(RuleBuilder ruleBuilder);
}
