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
package edu.samplu.krad.labs.lookups;

import org.junit.Test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class DemoLabsLookupMVViewSmokeTest extends DemoLabsLookupBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LabsLookup-AlwaysMultipleValuesSelectView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LabsLookup-AlwaysMultipleValuesSelectView&hideReturnLink=true";

    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        navigateToLookup("Lookup MV View");
    }

    @Test
    public void testLabsLookupMVViewBookmark() throws Exception {
        testLabsLookupMVView();
        passed();
    }

    @Test
    public void testLabsLookupMVViewNav() throws Exception {
        testLabsLookupMVView();
        passed();
    }
    
    protected void testLabsLookupMVView()throws Exception {
        waitAndTypeByName("lookupCriteria[fiscalOfficer.principalName]","fr*");
        waitAndClickButtonByText("Search");
        Thread.sleep(3000);
        assertTextPresent("5 items retrieved, displaying all items.");
    }
}