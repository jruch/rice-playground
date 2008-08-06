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
package org.kuali.rice.ken.services.impl;

import org.junit.Test;
import org.kuali.rice.ken.bo.Notification;
import org.kuali.rice.ken.bo.NotificationContentType;
import org.kuali.rice.ken.service.NotificationContentTypeService;
import org.kuali.rice.ken.test.NotificationTestCaseBase;
import org.kuali.rice.ken.test.TestConstants;
import org.kuali.rice.testharness.BaselineTestCase.BaselineMode;
import org.kuali.rice.testharness.BaselineTestCase.Mode;


/**
 * Tests NotificationContentTypeService implementation 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@BaselineMode(Mode.CLEAR_DB)
public class NotificationContentTypeServiceImplTest extends NotificationTestCaseBase {
    @Test public void testVersioning() {
        NotificationContentTypeService impl = services.getNotificationContentTypeService();
        int originalCurrentSize = impl.getAllCurrentContentTypes().size();
        int originalSize = impl.getAllContentTypes().size();

        Notification n = services.getNotificationService().getNotification(TestConstants.NOTIFICATION_1);
        
        NotificationContentType ct = n.getContentType(); 
        int originalVersion = ct.getVersion();
        assertTrue(ct.isCurrent());

        ct.setDescription("I was updated");
        impl.saveNotificationContentType(ct);
        
        assertEquals(originalCurrentSize, impl.getAllCurrentContentTypes().size());
        assertEquals(originalSize + 1, impl.getAllContentTypes().size());

        ct = impl.getNotificationContentType(ct.getName());
        assertEquals("I was updated", ct.getDescription());
        assertTrue(ct.isCurrent());
        assertEquals(originalVersion + 1, ct.getVersion().intValue());
        
        n = services.getNotificationService().getNotification(TestConstants.NOTIFICATION_1);
        NotificationContentType nct = n.getContentType();
        assertEquals(ct.getId(), nct.getId());
        assertEquals(ct.getVersion(), nct.getVersion());
        assertEquals(ct.isCurrent(), nct.isCurrent());
        assertEquals(originalVersion + 1, nct.getVersion().intValue());
        
        
    }

    @Test public void testUpdate() {
        NotificationContentTypeService impl = services.getNotificationContentTypeService();
        int originalCurrentSize = impl.getAllCurrentContentTypes().size();
        int originalSize = impl.getAllContentTypes().size();

        NotificationContentType type = new NotificationContentType();
        type.setDescription("blah");
        type.setName("test");
        type.setNamespace("test");
        type.setXsd("test");
        type.setXsl("test");

        impl.saveNotificationContentType(type);

        assertEquals(originalCurrentSize + 1, impl.getAllCurrentContentTypes().size());
        assertEquals(originalSize + 1, impl.getAllContentTypes().size());

        type = impl.getNotificationContentType("test");
        assertEquals("test", type.getName());
        assertEquals("blah", type.getDescription());
        assertEquals(true, type.isCurrent());
        assertEquals(Integer.valueOf(0), type.getVersion());

        type = new NotificationContentType();
        type.setDescription("blah 2");
        type.setName("test");
        type.setNamespace("test 2");
        type.setXsd("test 2");
        type.setXsl("test 2");

        impl.saveNotificationContentType(type);

        assertEquals(originalCurrentSize + 1, impl.getAllCurrentContentTypes().size());
        assertEquals(originalSize + 2, impl.getAllContentTypes().size());

        type = impl.getNotificationContentType("test");
        assertEquals("test", type.getName());
        assertEquals("blah 2", type.getDescription());
        assertEquals(true, type.isCurrent());
        assertEquals(Integer.valueOf(1), type.getVersion());
    }
}