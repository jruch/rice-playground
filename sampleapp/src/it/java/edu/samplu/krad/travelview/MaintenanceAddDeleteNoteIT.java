/*
 * Copyright 2006-2012 The Kuali Foundation
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
package edu.samplu.krad.travelview;

import edu.samplu.common.KradMenuITBase;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceAddDeleteNoteIT extends KradMenuITBase {

    @Override
    protected String getLinkLocator() {
        return "link=Travel Account Maintenance (New)";
    }

    @Test
    /**
     * Verify we can add and delete a note
     */
    public void testVerifyAddDeleteNote() throws Exception {
        gotoMenuLinkLocator();
        waitAndClick("css=div.tableborders.wrap.uif-boxLayoutVerticalItem.clearfix  span.uif-headerText-span > img.uif-disclosure-image");
        

        for (int second = 0;; second++) {
            if (second >= 15) {
                fail("timeout");
            }
            if (isElementPresent("css=button[title='Add a Note'].uif-action.uif-primaryActionButton.uif-smallActionButton")) {
                break;
            }

            Thread.sleep(1000);
        }

        waitAndClick("name=newCollectionLines['document.notes'].noteText");
        waitAndType("name=newCollectionLines['document.notes'].noteText", "Test note");
        waitAndClick("css=button[title='Add a Note'].uif-action.uif-primaryActionButton.uif-smallActionButton");
        for (int second = 0;; second++) {
            if (second >= 15) {
                fail("timeout, https://jira.kuali.org/browse/KULRICE-8038");
            }

            if (isElementPresent("name=document.notes[0].noteText")) {
                break;
            }

            Thread.sleep(1000);
        }

        assertEquals("Test note", getText("name=document.notes[0].noteText"));
        waitAndClick("css=button[title='Delete a Note'].uif-action.uif-primaryActionButton.uif-smallActionButton");

        for (int second = 0;; second++) {
            if (second >= 15) {
                fail("timeout");
            }

            if (!isElementPresent("name=document.notes[0].noteText")) {
                break;
            }

            Thread.sleep(1000);
        }
    }
}