/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.rule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttribute;
import org.kuali.rice.kew.rule.xmlrouting.GenericXMLRuleAttribute;
import org.kuali.rice.kew.util.KEWConstants;


/**
 * Standard rule expression implementation that evaluates the attributes associated with the rule definition 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
class WorkflowAttributeRuleExpression implements RuleExpression {
    
	public RuleExpressionResult evaluate(Rule rule, RouteContext context) {
        
		RuleBaseValues ruleDefinition = rule.getDefinition();
        boolean match = isMatch(ruleDefinition, context.getDocumentContent());
        if (match) {
            return new RuleExpressionResult(rule, match, ruleDefinition.getResponsibilities());
        }
		return new RuleExpressionResult(rule, match);
    }

    public boolean isMatch(RuleBaseValues ruleDefinition, DocumentContent docContent) {
        if (ruleDefinition.getRuleTemplate() == null) {
            // this can happen if neither rule template nor expression was specified in the rule definition
            // because WorkflowAttributeRuleExpression is the default expression implementation, we should
            // handle this here in order to support rules that fire unconditionally (and just return a statically
            // configured list of responsibilities)
            // the alternative is to either detect this situation in the rule xml parser or RuleImpl, and either substitute
            // a different RuleExpression implementation (a "default default" one) in the configuration, or at runtime,
            // that simply implements the one-liner of returning responsibilities.
            // doing this in the existing WorkflowAttributeRuleExpression implementation introduces the least change
            // or compatibilities issues, and avoids pushing compensating logic into RuleImpl
            return true;
        }
        for (Iterator iter = ruleDefinition.getRuleTemplate().getActiveRuleTemplateAttributes().iterator(); iter.hasNext();) {
            RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) iter.next();
            if (!ruleTemplateAttribute.isWorkflowAttribute()) {
                continue;
            }
            WorkflowAttribute routingAttribute = (WorkflowAttribute) ruleTemplateAttribute.getWorkflowAttribute();

            RuleAttribute ruleAttribute = ruleTemplateAttribute.getRuleAttribute();
            if (ruleAttribute.getType().equals(KEWConstants.RULE_XML_ATTRIBUTE_TYPE)) {
                ((GenericXMLRuleAttribute) routingAttribute).setRuleAttribute(ruleAttribute);
            }
            String className = ruleAttribute.getClassName();
            List editedRuleExtensions = new ArrayList();
            for (Iterator iter2 = ruleDefinition.getRuleExtensions().iterator(); iter2.hasNext();) {
                RuleExtension extension = (RuleExtension) iter2.next();
                if (extension.getRuleTemplateAttribute().getRuleAttribute().getClassName().equals(className)) {
                    editedRuleExtensions.add(extension);
                }
            }
            if (!routingAttribute.isMatch(docContent, editedRuleExtensions)) {
                return false;
            }
        }
        return true;
    }

}