/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package edu.iu.uis.eden.messaging.exceptionhandling;

import org.apache.commons.lang.StringUtils;
import org.kuali.bus.services.KSBServiceLocator;
import org.kuali.rice.kew.exception.WorkflowRuntimeException;

import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.messaging.PersistedMessage;

/**
 * A {@link MessageExceptionHandler} which handles putting documents into exception routing.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class DocumentMessageExceptionHandler extends DefaultMessageExceptionHandler {

    @Override
    protected void placeInException(Throwable throwable, PersistedMessage message) throws Exception {
	KEWServiceLocator.getExceptionRoutingService()
		.placeInExceptionRouting(throwable, message, getRouteHeaderId(message));
    }

    @Override
    protected void scheduleExecution(Throwable throwable, PersistedMessage message) throws Exception {
	String description = "DocumentId: " + getRouteHeaderId(message);
	KSBServiceLocator.getExceptionRoutingService().scheduleExecution(throwable, message, description);
    }

    protected Long getRouteHeaderId(PersistedMessage message) {
	if (!StringUtils.isEmpty(message.getValue1()) && StringUtils.isNumeric(message.getValue1())) {
	    return Long.valueOf(message.getValue1());
	}
	throw new WorkflowRuntimeException("Unable to put this message in exception routing service name " + message.getServiceName());
    }
}