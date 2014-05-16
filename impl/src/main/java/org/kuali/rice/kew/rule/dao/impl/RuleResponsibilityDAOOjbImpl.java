/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.rule.dao.impl;

import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.rice.kew.rule.RuleResponsibility;
import org.kuali.rice.kew.rule.dao.RuleResponsibilityDAO;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;


public class RuleResponsibilityDAOOjbImpl extends PersistenceBrokerDaoSupport implements RuleResponsibilityDAO {

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.rice.kew.rule.dao.RuleTemplateAttributeDAO#delete(java.lang.Long)
     */
    public void delete(Long ruleResponsibilityId) {
        this.getPersistenceBrokerTemplate().delete(findByRuleResponsibilityId(ruleResponsibilityId));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.rice.kew.rule.dao.RuleTemplateAttributeDAO#findByRuleTemplateAttributeId(java.lang.Long)
     */
    public RuleResponsibility findByRuleResponsibilityId(Long ruleResponsibilityId) {
        RuleResponsibility ruleResponsibility = new RuleResponsibility();
        ruleResponsibility.setRuleResponsibilityKey(ruleResponsibilityId);
        return (RuleResponsibility) this.getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(ruleResponsibility));
    }
}