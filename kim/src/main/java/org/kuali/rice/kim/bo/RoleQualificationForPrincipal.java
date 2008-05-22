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
package org.kuali.rice.kim.bo;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.kuali.core.util.TypedArrayList;

/**
 * Primarily a helper business object that provides a list of qualified role attributes for 
 * a specific principal and role.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RoleQualificationForPrincipal extends Role {
    private static final long serialVersionUID = 6701917498866245651L;
    
    private Long principalId;
    
    private ArrayList<PrincipalQualifiedRoleAttribute> qualifiedRoleAttributes;

    public RoleQualificationForPrincipal() {
        super();
        this.qualifiedRoleAttributes = new TypedArrayList(PrincipalQualifiedRoleAttribute.class);
    }

    /**
     * @return the principalId
     */
    public Long getPrincipalId() {
        return this.principalId;
    }

    /**
     * @param principalId the principalId to set
     */
    public void setPrincipalId(Long principalId) {
        this.principalId = principalId;
    }

    /**
     * @return the qualifiedRoleAttributes
     */
    public ArrayList<PrincipalQualifiedRoleAttribute> getQualifiedRoleAttributes() {
        return this.qualifiedRoleAttributes;
    }

    /**
     * @param qualifiedRoleAttributes the qualifiedRoleAttributes to set
     */
    public void setQualifiedRoleAttributes(ArrayList<PrincipalQualifiedRoleAttribute> qualifiedRoleAttributes) {
        this.qualifiedRoleAttributes = qualifiedRoleAttributes;
    }

    /**
     * This overridden method ...
     * 
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        return null;
    }
}