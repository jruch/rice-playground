/*
 * Copyright 2009 The Kuali Foundation.
 *
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
package org.kuali.rice.kew.responsibility.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.kuali.rice.core.database.platform.Platform;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.responsibility.dao.ResponsibilityIdDAO;

public class ResponsibilityIdDAOJpaImpl implements ResponsibilityIdDAO {
    @PersistenceContext(unitName = "kew-unit")
    private EntityManager entityManager;


	public Long getNewResponsibilityId() {
	    return getPlatform().getNextValSQL("KREW_RSP_S", entityManager);
    }

	protected Platform getPlatform() {
    	return (Platform)GlobalResourceLoader.getService(RiceConstants.DB_PLATFORM);
    }

}
