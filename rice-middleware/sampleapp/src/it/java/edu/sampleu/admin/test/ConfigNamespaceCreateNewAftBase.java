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
package edu.sampleu.admin.test;

import org.kuali.rice.testtools.common.Failable;
import org.kuali.rice.testtools.selenium.ITUtil;
import org.kuali.rice.testtools.selenium.WebDriverUtil;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class ConfigNamespaceCreateNewAftBase extends AdminTmplMthdAftNavBase {

    /**
     * ITUtil.PORTAL+"?channelTitle=Namespace&channelUrl="+WebDriverUtil.getBaseUrlString()+
     * "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.namespace.NamespaceBo&docFormKey=88888888&returnLocation="+
     * +ITUtil.PORTAL_URL+ ITUtil.HIDE_RETURN_LINK;
     */   
    public static final String BOOKMARK_URL = ITUtil.PORTAL+"?channelTitle=Namespace&channelUrl="+ WebDriverUtil
            .getBaseUrlString()+"/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.coreservice.impl.namespace.NamespaceBo&docFormKey=88888888&returnLocation="+
            ITUtil.PORTAL_URL+ ITUtil.HIDE_RETURN_LINK;

    /**
     * {@inheritDoc}
     * Namespace
     * @return
     */
    @Override
    protected String getLinkLocator() {
        return "Namespace";
    }

    public void testConfigNamespaceCreateNewBookmark(Failable failable) throws Exception {
        testConfigNamespaceCreateNew();
        passed();
    }

    public void testConfigNamespaceCreateNewNav(Failable failable) throws Exception {
        testConfigNamespaceCreateNew();
        passed();
    }    
    
    public void testConfigNamespaceCreateNew() throws Exception
    {
        selectFrameIframePortlet();
        waitAndClickByXpath(CREATE_NEW_XPATH);        
        waitAndTypeByName("document.documentHeader.documentDescription", "Test description of Namespace create new " + ITUtil.createUniqueDtsPlusTwoRandomCharsNot9Digits());
        waitAndTypeByName("document.newMaintainableObject.code", "KR-SYS3" + ITUtil.createUniqueDtsPlusTwoRandomChars());
        waitAndTypeByName("document.newMaintainableObject.name","Enterprise Infrastructure 3");
        waitAndTypeByName("document.newMaintainableObject.applicationId","RICE");
        waitAndClickByName("methodToCall.route");
        checkForDocError();
        waitAndClickByName("methodToCall.close");
        waitAndClickByName("methodToCall.processAnswer.button1");        
    }
}