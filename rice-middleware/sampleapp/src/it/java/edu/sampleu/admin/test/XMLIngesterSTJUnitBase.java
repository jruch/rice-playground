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

import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

/**
 * Abstract base class for JUnit LoginLogout Smoke Tests.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class XMLIngesterSTJUnitBase extends XmlIngesterAftBase {

    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

    @Override
    protected File newTempFile(String name) throws IOException {
        return folder.newFile(name);
    }

    @Override
    public void fail(String string) {
        Assert.fail(string);
    }
}