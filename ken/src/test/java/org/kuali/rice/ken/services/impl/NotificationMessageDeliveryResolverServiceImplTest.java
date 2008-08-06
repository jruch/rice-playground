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

import java.util.Collection;
import java.util.HashMap;

import org.apache.ojb.broker.query.Criteria;
import org.junit.Ignore;
import org.junit.Test;
import org.kuali.rice.core.dao.GenericDao;
import org.kuali.rice.kcb.GlobalKCBServiceLocator;
import org.kuali.rice.kcb.bo.Message;
import org.kuali.rice.kcb.service.MessageService;
import org.kuali.rice.ken.bo.Notification;
import org.kuali.rice.ken.bo.NotificationMessageDelivery;
import org.kuali.rice.ken.service.NotificationMessageDeliveryResolverService;
import org.kuali.rice.ken.service.NotificationRecipientService;
import org.kuali.rice.ken.service.NotificationService;
import org.kuali.rice.ken.service.ProcessingResult;
import org.kuali.rice.ken.service.UserPreferenceService;
import org.kuali.rice.ken.service.impl.NotificationMessageDeliveryResolverServiceImpl;
import org.kuali.rice.ken.test.NotificationTestCaseBase;
import org.kuali.rice.ken.util.NotificationConstants;
import org.kuali.rice.testharness.data.PerTestUnitTestData;
import org.kuali.rice.testharness.data.UnitTestData;
import org.springframework.transaction.PlatformTransactionManager;

import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;

/**
 * Tests NotificationMessageDeliveryResolverServiceImpl
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
// deadlocks are detected during clear database lifecycle (even when select for update is commented out...)
// Make sure KCB has some deliverers configured for the test users, so message deliveries get created and the messages aren't removed
@PerTestUnitTestData({
    @UnitTestData("insert into KCB_RECIP_DELIVS (ID, RECIPIENT_ID, CHANNEL, DELIVERER_NAME, DB_LOCK_VER_NBR) values (1, 'TestUser6', 'KEW', 'mock', 0)"),
    @UnitTestData("insert into KCB_RECIP_DELIVS (ID, RECIPIENT_ID, CHANNEL, DELIVERER_NAME, DB_LOCK_VER_NBR) values (2, 'TestUser1', 'KEW', 'mock', 0)"),
    @UnitTestData("insert into KCB_RECIP_DELIVS (ID, RECIPIENT_ID, CHANNEL, DELIVERER_NAME, DB_LOCK_VER_NBR) values (3, 'TestUser2', 'KEW', 'mock', 0)"),
    @UnitTestData("insert into KCB_RECIP_DELIVS (ID, RECIPIENT_ID, CHANNEL, DELIVERER_NAME, DB_LOCK_VER_NBR) values (4, 'quickstart', 'KEW', 'mock', 0)"),
    @UnitTestData("insert into KCB_RECIP_DELIVS (ID, RECIPIENT_ID, CHANNEL, DELIVERER_NAME, DB_LOCK_VER_NBR) values (5, 'TestUser5', 'KEW', 'mock', 0)"),
    @UnitTestData("insert into KCB_RECIP_DELIVS (ID, RECIPIENT_ID, CHANNEL, DELIVERER_NAME, DB_LOCK_VER_NBR) values (6, 'TestUser4', 'KEW', 'mock', 0)")
})
public class NotificationMessageDeliveryResolverServiceImplTest extends NotificationTestCaseBase {
    // NOTE: this value is HIGHLY dependent on the test data, make sure that it reflects the results
    // expected from the test data
    private static final int EXPECTED_SUCCESSES = 6;
    
    /**
     * Id of notification for which we will intentionally generate an exception during processing
     */
    private static final long BAD_NOTIFICATION_ID = 3L;
    
    private static class TestNotificationMessageDeliveryResolverService extends NotificationMessageDeliveryResolverServiceImpl {
        public TestNotificationMessageDeliveryResolverService(NotificationService notificationService, NotificationRecipientService notificationRecipientService, 
                GenericDao businessObjectDao, PlatformTransactionManager txManager, ExecutorService executor, UserPreferenceService userPreferenceService) {
            super(notificationService, notificationRecipientService, businessObjectDao, txManager, executor, userPreferenceService);
        }

        @Override
        protected Collection<Object> processWorkItems(Collection<Notification> notifications) {
            for (Notification notification: notifications) {
                if (notification.getId().longValue() == BAD_NOTIFICATION_ID) {
                    throw new RuntimeException("Intentional heinous exception");
                }
            }
            return super.processWorkItems(notifications);
        }
    }

    protected TestNotificationMessageDeliveryResolverService getResolverService() {
        return new TestNotificationMessageDeliveryResolverService(services.getNotificationService(), services.getNotificationRecipientService(), services.getGenericDao(), transactionManager, 
        	Executors.newFixedThreadPool(5), services.getUserPreferenceService());
    }

    protected void assertProcessResults() {
        // one error should have occurred and the delivery should have been marked unlocked again
        Criteria criteria = new Criteria();
        criteria.addNotNull(NotificationConstants.BO_PROPERTY_NAMES.LOCKED_DATE);
        Collection<NotificationMessageDelivery> lockedDeliveries = services.getGenericDao().findMatching(Notification.class, criteria);
        assertEquals(0, lockedDeliveries.size());

        // should be 1 unprocessed delivery (the one that had an error)
        HashMap<String, String> queryCriteria = new HashMap<String, String>();
        queryCriteria.put(NotificationConstants.BO_PROPERTY_NAMES.PROCESSING_FLAG, NotificationConstants.PROCESSING_FLAGS.UNRESOLVED);
        Collection<Notification> unprocessedDeliveries = services.getGenericDao().findMatching(Notification.class, queryCriteria);
        assertEquals(1, unprocessedDeliveries.size());
        Notification n = unprocessedDeliveries.iterator().next();
        // #3 is the bad one
        assertEquals(BAD_NOTIFICATION_ID, n.getId().longValue());
    }

    /**
     * Test resolution of notifications
     * This test resolves UNRESOLVED notification ids #3 and #4 in the test data.  An artificial exception is generated for notification #3.
     * For notification #4, the recipients are defined to be the Rice Team and TestUser1.  This results in 8 recipient resolutions, two of which
     * are Email deliveries for jaf30 and ag266.
     * If you change the test data this test should be updated to reflect the expected results.
     */
    @Test
    public void testResolveNotificationMessageDeliveries() throws Exception {
        NotificationMessageDeliveryResolverService nSvc = getResolverService();

        long start = System.currentTimeMillis();
        ProcessingResult result = nSvc.resolveNotificationMessageDeliveries();
        
        Thread.sleep(20000);

        for (Object message: result.getSuccesses()) {
            LOG.info("Message before: " + message);
        }

        assertEquals(EXPECTED_SUCCESSES, result.getSuccesses().size());

        Collection<NotificationMessageDelivery> ds = services.getNotificationMessageDeliveryService().getNotificationMessageDeliveries();

        MessageService ms = (MessageService) GlobalKCBServiceLocator.getInstance().getMessageService();
        for (Message m: ms.getAllMessages()) {
            LOG.info("Message after: " + m);
        }
        assertEquals(result.getSuccesses().size(), ms.getAllMessages().size());
        
        assertProcessResults();
    }

    
    /**
     * Test concurrent resolution of notifications
     */
    @Test
    public void testResolverConcurrency() throws InterruptedException {
        final NotificationMessageDeliveryResolverService nSvc = getResolverService();

        final ProcessingResult[] results = new ProcessingResult[2];
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    results[0] = nSvc.resolveNotificationMessageDeliveries();
                } catch (Exception e) {
                    System.err.println("Error resolving notification message deliveries");
                    e.printStackTrace();
                }
            }
        });
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                try {
                    results[1] = nSvc.resolveNotificationMessageDeliveries();
                } catch (Exception e) {
                    System.err.println("Error resolving notification message deliveries");
                    e.printStackTrace();
                }
            }
        });
        
        t1.start();
        t2.start();
        
        t1.join();
        t2.join();
        
        // assert that ONE of the resolvers got all the items, and the other got NONE of the items
        LOG.info("Results of thread #1: " + results[0]);
        LOG.info("Results of thread #2: " + results[1]);
        assertNotNull(results[0]);
        assertNotNull(results[1]);
        assertTrue((results[0].getSuccesses().size() == EXPECTED_SUCCESSES && results[0].getFailures().size() == 1 && results[1].getSuccesses().size() == 0 && results[1].getFailures().size() == 0) ||
                   (results[1].getSuccesses().size() == EXPECTED_SUCCESSES && results[1].getFailures().size() == 1 && results[0].getSuccesses().size() == 0 && results[0].getFailures().size() == 0));
        
        assertProcessResults();
    }
}