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
package org.kuali.rice.kim.util;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;


/**
 * This is a description of what this class does - bhargavp don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimCommonUtils {

	public static boolean matchInputWithWildcard(String inputStr, String matchStr){
		inputStr = inputStr.replaceAll("\\*", ".*");
		if(matchStr.matches(inputStr)){
			return true;
		}
		return false;
	}

	/**
	 * 
	 * This method traverses the document type hierarchy
	 * 
	 * @param currentDocType
	 * @param documentTypeName
	 * @return
	 */
	public static boolean isParentDocument(DocumentType currentDocType, String documentTypeName) {
		if (currentDocType != null) {
			if (documentTypeName.equalsIgnoreCase(currentDocType.getName())) {
				return true;
			} else if (currentDocType.getDocTypeParentId() != null 
					&& !currentDocType.getDocumentTypeId().equals(currentDocType.getDocTypeParentId())) {
				return isParentDocument(currentDocType.getParentDocType(), documentTypeName);
			}
		}
		return false;
	}
	
	public static boolean doesPropertyNameMatch(String requestedDetailsPropertyName, String permissionDetailsPropertyName){
		if(StringUtils.isEmpty(permissionDetailsPropertyName))
			return true;
		return requestedDetailsPropertyName.equals(permissionDetailsPropertyName) 
			|| (requestedDetailsPropertyName.startsWith(permissionDetailsPropertyName) 
					&& (requestedDetailsPropertyName.substring(
							requestedDetailsPropertyName.indexOf(permissionDetailsPropertyName)+permissionDetailsPropertyName.length()).indexOf(".")!=-1));
	}
}
