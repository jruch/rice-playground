/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package edu.iu.uis.eden.actions;

import java.util.Collection;

import org.junit.Test;
import org.kuali.workflow.test.KEWTestCase;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.clientapp.WorkflowDocument;
import edu.iu.uis.eden.clientapp.vo.NetworkIdVO;
import edu.iu.uis.eden.exception.WorkflowException;

public class RouteDocumentTest extends KEWTestCase {

	public static final String DOCUMENT_TYPE_NAME = "BlanketApproveSequentialTest";
    public static final String DOCUMENT_TYPE_POLICY_TEST_NAME = "BlanketApprovePolicyTest";
    
    protected void loadTestData() throws Exception {
        loadXmlFile("ActionsConfig.xml");
    }
        
	/**
     * Tests that an exception is thrown if you try to execute a "route" command on an already routed document.
     */
    @Test public void testRouteAlreadyRoutedDocument() throws Exception {
    	WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user1"), DOCUMENT_TYPE_NAME);
    	document.routeDocument("");
    	
    	assertTrue("Document should be ENROUTE.", document.stateIsEnroute());
    	assertFalse("There should not be a request to ewestfal.", document.isApprovalRequested());
    	
    	// verify that only 1 action taken has been performed
    	Collection actionTakens = KEWServiceLocator.getActionTakenService().findByRouteHeaderId(document.getRouteHeaderId());
    	assertEquals("There should be only 1 action taken.", 1, actionTakens.size());
    	
    	// now try and route the document again, an exception should be thrown
    	try {
    		document.routeDocument("");
    		fail("A WorkflowException should have been thrown.");
    	} catch (WorkflowException e) {
    		e.printStackTrace();
    	}
    	
    	// verify that there is still only 1 action taken (the transaction above should have rolled back)
    	actionTakens = KEWServiceLocator.getActionTakenService().findByRouteHeaderId(document.getRouteHeaderId());
    	assertEquals("There should still be only 1 action taken.", 1, actionTakens.size());
    }
	
    /**
     * Tests that an exception is not thrown if you try to execute a "route" command on a document you did not initiate.
     */
    @Test public void testRouteDocumentAsNonInitiatorUser() throws Exception {
        WorkflowDocument firstDocument = new WorkflowDocument(new NetworkIdVO("user1"), DOCUMENT_TYPE_POLICY_TEST_NAME);
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user2"), firstDocument.getRouteHeaderId());
        try {
            document.routeDocument("");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception thrown but should not have have been... Exception was of type " + e.getClass().getName() + " and message was " + e.getMessage());
        }
        document = new WorkflowDocument(new NetworkIdVO("user1"), firstDocument.getRouteHeaderId());
        assertEquals("Document should be in Enroute status.", EdenConstants.ROUTE_HEADER_ENROUTE_CD, document.getRouteHeader().getDocRouteStatus());

        // verify that there is 1 action taken
        Collection actionTakens = KEWServiceLocator.getActionTakenService().findByRouteHeaderId(document.getRouteHeaderId());
        assertEquals("There should be 1 action taken.", 1, actionTakens.size());
    }
    
    /**
     * Tests that an exception is not thrown if you try to execute a "route" command on a document you did not initiate.
     */
    @Test public void testRouteDefaultDocumentAsNonInitiatorUser() throws Exception {
        WorkflowDocument firstDocument = new WorkflowDocument(new NetworkIdVO("user1"), DOCUMENT_TYPE_NAME);
        WorkflowDocument document = new WorkflowDocument(new NetworkIdVO("user2"), firstDocument.getRouteHeaderId());
        try {
            document.routeDocument("");
            fail("Exception should have been thrown.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertFalse("Document should not be ENROUTE.", document.stateIsEnroute());
        assertFalse("There should not be a request to user2.", document.isApprovalRequested());
        
        // verify that there are no actions taken
        Collection actionTakens = KEWServiceLocator.getActionTakenService().findByRouteHeaderId(document.getRouteHeaderId());
        assertEquals("There should be 0 actions taken.", 0, actionTakens.size());
    }
    
}
