/**
 * Copyright 2005-2011 The Kuali Foundation
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

import org.junit.Assert;
import org.junit.Test;
import org.kuali.rice.testtools.selenium.ITUtil;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;
import org.kuali.rice.testtools.selenium.WebDriverUtil;
import org.openqa.selenium.By;

/**
 * tests creating and cancelling new and edit Role maintenance screens
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class IdentityPersonRoleAft extends WebDriverLegacyITBase{

    public static final String EDIT_URL = WebDriverUtil.getBaseUrlString() + "/kim/identityManagementPersonDocument.do?returnLocation=" + ITUtil.PORTAL_URL_ENCODED + "&principalId=LTID&docTypeName=IdentityManagementPersonDocument&methodToCall=docHandler&command=initiate";
    public static final String BOOKARM_URL = ITUtil.PORTAL + "?channelTitle=Person&channelUrl=" + WebDriverUtil.getBaseUrlString() +
            "/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.api.identity.Person&docFormKey=88888888&returnLocation=" +
            ITUtil.PORTAL_URL + "&hideReturnLink=true";
    private int userCnt = Integer.valueOf(System.getProperty("test.role.user.cnt", "176"));
    private int userCntStart = Integer.valueOf(System.getProperty("test.role.user.cnt.start", "0"));
    private String idBase = System.getProperty("test.role.user.base", "lt");
    public static final String ADMIN_ROLE_ID = "63";
    public static final String KRMS_ADMIN_ROLE_ID = "98";

    @Override
    public void fail(String message) {
        Assert.fail(message);
    }

    @Override
    protected String getBookmarkUrl() {
        return BOOKARM_URL;
    }

    @Test
    public void testPersonRole() throws InterruptedException {
        String id = "";
        String format = "%0" + (userCnt + "").length() + "d";
        for(int i = userCntStart; i < userCnt; i++) {
            id = idBase + String.format(format, i);
            open(EDIT_URL.replace("LTID", id));
            checkForIncidentReport();
            waitAndTypeByName("document.documentHeader.documentDescription", "Admin permissions for " + id); // don't make unique

            selectByName("newAffln.affiliationTypeCode", "Affiliate");
            selectOptionByName("newAffln.campusCode", "BL");
            checkByName("newAffln.dflt");
            waitAndClickByName("methodToCall.addAffln.anchor");

            waitAndClick(By.id("tab-Membership-imageToggle"));
            waitAndType(By.id("newRole.roleId"), ADMIN_ROLE_ID);
            driver.findElement(By.name("methodToCall.addRole.anchor")).click();

            waitAndType(By.id("newRole.roleId"), KRMS_ADMIN_ROLE_ID);
            driver.findElement(By.name("methodToCall.addRole.anchor")).click();
            waitAndClickByName("methodToCall.blanketApprove");
            waitForPageToLoad();
        }
        passed();
    }
}