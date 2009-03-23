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
package org.kuali.rice.kns.util;

import java.util.List;
import java.util.Set;

/**
 * An implementation of {@link MessageContainer} that makes warning messages accessible by the JSP layer 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class WarningContainer extends MessageContainer {

	public WarningContainer(ErrorMap errorMap) {
		super(errorMap);
	}
	
	/**
	 * This overridden method ...
	 * 
	 * @see org.kuali.rice.kns.util.MessageContainer#getMessageCount()
	 */
	@Override
	public int getMessageCount() {
		return getErrorMap().getWarningCount();
	}

	
	/**
	 * @see org.kuali.rice.kns.util.MessageContainer#getMessagePropertyNames()
	 */
	@Override
	protected Set<String> getMessagePropertyNames() {
		return getErrorMap().getAllPropertiesWithWarnings();
	}

	/**
	 * @see org.kuali.rice.kns.util.MessageContainer#getMessagePropertyList()
	 */
	@Override
	public List<String> getMessagePropertyList() {
		return getErrorMap().getPropertiesWithWarnings();
	}
	
	/**
	 * @see org.kuali.rice.kns.util.MessageContainer#getMessagesForProperty(java.lang.String)
	 */
	@Override
	protected List getMessagesForProperty(String propertyName) {
		return getErrorMap().getWarningMessagesForProperty(propertyName);
	}
}
