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
package org.kuali.rice.kew.rule;

import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kuali.rice.core.jpa.annotations.Sequence;
import org.kuali.rice.core.reflect.ObjectDefinition;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.bo.KewPersistableBusinessObjectBase;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.service.KIMServiceLocator;


/**
 * A model bean representing the responsibility of a user, workgroup, or role
 * to perform some action on a document.  Used by the rule system to
 * identify the appropriate responsibile parties to generate
 * {@link ActionRequestValue}s to.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREW_RULE_RSP_T")
@Sequence(name="KREW_RSP_S", property="ruleResponsibilityKey")
public class RuleResponsibility extends KewPersistableBusinessObjectBase {

	private static final long serialVersionUID = -1565688857123316797L;
	@Id
	@Column(name="RULE_RSP_ID")
	private Long ruleResponsibilityKey;
    @Column(name="RSP_ID")
	private Long responsibilityId;
    @Column(name="RULE_ID", insertable=false, updatable=false)
    private Long ruleBaseValuesId;
    @Column(name="ACTN_RQST_CD")
	private String actionRequestedCd;
    @Column(name="NM")
	private String ruleResponsibilityName;
    @Column(name="TYP")
	private String ruleResponsibilityType;
    @Column(name="PRIO")
	private Integer priority;
    @Column(name="APPR_PLCY")
	private String approvePolicy;

    @ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="RULE_ID")
	private RuleBaseValues ruleBaseValues;
    //@OneToMany(fetch=FetchType.EAGER,cascade={CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE},
    //        mappedBy="ruleResponsibility")
    //private List<RuleDelegation> delegationRules = new ArrayList<RuleDelegation>();

    public KimPrincipal getPrincipal() 
    {
    	if (isUsingWorkflowUser()) {
    		return KEWServiceLocator.getIdentityHelperService().getPrincipal(ruleResponsibilityName);
    	}
    	return null;
    }

    public Group getGroup() {
        if (isUsingGroup()) {
        	return KIMServiceLocator.getIdentityManagementService().getGroup(ruleResponsibilityName);
        }
        return null;
    }

    public String getRole() {
        if (isUsingRole()) {
            return ruleResponsibilityName;
        }
        return null;
    }

    public String getResolvedRoleName() {
        if (isUsingRole()) {
            return getRole().substring(getRole().indexOf("!") + 1, getRole().length());
        }
        return null;
    }

    public String getRoleAttributeName() {
	return getRole().substring(0, getRole().indexOf("!"));
    }
    
    public RoleAttribute resolveRoleAttribute() throws WorkflowException {
        if (isUsingRole()) {
            String attributeName = getRoleAttributeName();
            return (RoleAttribute) GlobalResourceLoader.getResourceLoader().getObject(new ObjectDefinition(attributeName));
        }
        return null;
    }

    public boolean isUsingRole() {
    	return (ruleResponsibilityName != null && ruleResponsibilityType != null && ruleResponsibilityType.equals(KEWConstants.RULE_RESPONSIBILITY_ROLE_ID));
    }

    public boolean isUsingWorkflowUser() {
    	return (ruleResponsibilityName != null && !ruleResponsibilityName.trim().equals("") && ruleResponsibilityType != null && ruleResponsibilityType.equals(KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID));
    }

    public boolean isUsingGroup() {
    	return (ruleResponsibilityName != null && !ruleResponsibilityName.trim().equals("") && ruleResponsibilityType != null && ruleResponsibilityType.equals(KEWConstants.RULE_RESPONSIBILITY_GROUP_ID));
    }

    public Long getRuleBaseValuesId() {
        return ruleBaseValuesId;
    }

    public void setRuleBaseValuesId(Long ruleBaseValuesId) {
        this.ruleBaseValuesId = ruleBaseValuesId;
    }

    public RuleBaseValues getRuleBaseValues() {
        return ruleBaseValues;
    }

    public void setRuleBaseValues(RuleBaseValues ruleBaseValues) {
        this.ruleBaseValues = ruleBaseValues;
    }

    public String getActionRequestedCd() {
        return actionRequestedCd;
    }

    public void setActionRequestedCd(String actionRequestedCd) {
        this.actionRequestedCd = actionRequestedCd;
    }

    public Long getRuleResponsibilityKey() {
        return ruleResponsibilityKey;
    }

    public void setRuleResponsibilityKey(Long ruleResponsibilityId) {
        this.ruleResponsibilityKey = ruleResponsibilityId;
    }
    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getApprovePolicy() {
        return approvePolicy;
    }

    public void setApprovePolicy(String approvePolicy) {
        this.approvePolicy = approvePolicy;
    }

    public Object copy(boolean preserveKeys) {
        RuleResponsibility ruleResponsibilityClone = new RuleResponsibility();
        ruleResponsibilityClone.setApprovePolicy(getApprovePolicy());
        if (actionRequestedCd != null) {
            ruleResponsibilityClone.setActionRequestedCd(actionRequestedCd);
        }
        if (ruleResponsibilityKey != null && preserveKeys) {
            ruleResponsibilityClone.setRuleResponsibilityKey(ruleResponsibilityKey);
        }

        if (responsibilityId != null) {
            ruleResponsibilityClone.setResponsibilityId(responsibilityId);
        }

        if (ruleResponsibilityName != null) {
            ruleResponsibilityClone.setRuleResponsibilityName(ruleResponsibilityName);
        }
        if (ruleResponsibilityType != null) {
            ruleResponsibilityClone.setRuleResponsibilityType(ruleResponsibilityType);
        }
        if (priority != null) {
            ruleResponsibilityClone.setPriority(priority);
        }
//        if (delegationRules != null) {
//            for (Iterator iter = delegationRules.iterator(); iter.hasNext();) {
//                RuleDelegation delegation = (RuleDelegation) iter.next();
//                RuleDelegation delegationClone = (RuleDelegation)delegation.copy(preserveKeys);
//                delegationClone.setRuleResponsibility(ruleResponsibilityClone);
//                ruleResponsibilityClone.getDelegationRules().add(delegationClone);
//
//            }
//        }
        return ruleResponsibilityClone;
    }

    public String getRuleResponsibilityName() {
        return ruleResponsibilityName;
    }

    public void setRuleResponsibilityName(String ruleResponsibilityName) {
        this.ruleResponsibilityName = ruleResponsibilityName;
    }

    public String getRuleResponsibilityType() {
        return ruleResponsibilityType;
    }

    public void setRuleResponsibilityType(String ruleResponsibilityType) {
        this.ruleResponsibilityType = ruleResponsibilityType;
    }

    public Long getResponsibilityId() {
        return responsibilityId;
    }
    public void setResponsibilityId(Long responsibilityId) {
        this.responsibilityId = responsibilityId;
    }
    
    public List<RuleDelegation> getDelegationRules() {
    	return KEWServiceLocator.getRuleDelegationService().findByResponsibilityId(getResponsibilityId());
    }
    
    public RuleDelegation getDelegationRule(int index) {
    	return getDelegationRules().get(index);
    }
    
//    public boolean isDelegating() {
//        return !getDelegationRules().isEmpty();
//    }
//
//    public List getDelegationRules() {
//        return delegationRules;
//    }
//    public void setDelegationRules(List delegationRules) {
//        this.delegationRules = delegationRules;
//    }
//
//    public RuleDelegation getDelegationRule(int index) {
//        while (getDelegationRules().size() <= index) {
//            RuleDelegation ruleDelegation = new RuleDelegation();
//            ruleDelegation.setRuleResponsibility(this);
//            ruleDelegation.setDelegationRuleBaseValues(new RuleBaseValues());
//            getDelegationRules().add(ruleDelegation);
//        }
//        return (RuleDelegation) getDelegationRules().get(index);
//    }
    
    // convenience methods for the web-tier
    
    public String getActionRequestedDisplayValue() {
    	return KEWConstants.ACTION_REQUEST_CODES.get(getActionRequestedCd());
    }
    
    public String getRuleResponsibilityTypeDisplayValue() {
    	return KEWConstants.RULE_RESPONSIBILITY_TYPES.get(getRuleResponsibilityType());
    }
    
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof RuleResponsibility)) return false;
        RuleResponsibility pred = (RuleResponsibility) o;
        return Utilities.equals(ruleResponsibilityName, pred.getRuleResponsibilityName()) &&
               Utilities.equals(actionRequestedCd, pred.getActionRequestedCd()) &&
               Utilities.equals(priority, pred.getPriority()) &&
               Utilities.equals(approvePolicy, pred.getApprovePolicy());
    }
    
    /**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
		.append(this.actionRequestedCd)
		.append(this.approvePolicy)
		.append(this.priority)
		.append(this.ruleResponsibilityName).toHashCode();
	}
    
    

    @Override
	protected LinkedHashMap toStringMapper() {
    	LinkedHashMap map = new LinkedHashMap();
    	map.put("responsibilityId", responsibilityId);
    	map.put("ruleResponsibilityKey", ruleResponsibilityKey);
    	map.put("ruleResponsibilityName", ruleResponsibilityName);
    	map.put("ruleResponsibilityType", ruleResponsibilityType);
    	map.put("ruleBaseValuesId", ruleBaseValuesId);
    	map.put("actionRequestedCd", actionRequestedCd);
    	map.put("priority", priority);
    	return map;
	}
    
    
}