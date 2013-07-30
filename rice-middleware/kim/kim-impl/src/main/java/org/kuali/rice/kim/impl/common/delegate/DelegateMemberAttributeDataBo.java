/**
 * Copyright 2005-2013 The Kuali Foundation
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
package org.kuali.rice.kim.impl.common.delegate;

import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_DLGN_MBR_ATTR_DATA_T")
public class DelegateMemberAttributeDataBo extends KimAttributeDataBo {

    @Id
    @GeneratedValue(generator = "KRIM_ATTR_DATA_ID_S")
    @Column(name="ATTR_DATA_ID")
    private String id;

    @Column(name = "DLGN_MBR_ID")
    private String assignedToId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getAssignedToId() {
        return assignedToId;
    }

    @Override
    public void setAssignedToId(String assignedToId) {
        this.assignedToId = assignedToId;
    }

}
