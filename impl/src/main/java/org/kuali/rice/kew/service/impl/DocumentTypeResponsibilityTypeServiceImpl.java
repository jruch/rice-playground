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
package org.kuali.rice.kew.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimResponsibilityTypeServiceBase;
import org.kuali.rice.kim.util.KimCommonUtils;

/**
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentTypeResponsibilityTypeServiceImpl extends KimResponsibilityTypeServiceBase {
	
	/**
	 * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
	 */
	@Override
	protected boolean performMatch(AttributeSet inputAttributeSet, AttributeSet storedAttributeSet) {
		return (inputAttributeSet.get(KimAttributes.DOCUMENT_TYPE_NAME).equals(storedAttributeSet.get(KimAttributes.DOCUMENT_TYPE_NAME)) || KimCommonUtils.isParentDocument(KEWServiceLocator.getDocumentTypeService().findByName(inputAttributeSet.get(KimAttributes.DOCUMENT_TYPE_NAME)), storedAttributeSet.get(KimAttributes.DOCUMENT_TYPE_NAME))); 
	}
}
