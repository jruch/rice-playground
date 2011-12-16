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
package org.kuali.rice.kew.impl.document.search

import org.junit.Test
import org.kuali.rice.kew.api.document.DocumentStatus
import org.kuali.rice.kew.api.document.DocumentStatusCategory
import org.kuali.rice.kew.api.KEWPropertyConstants
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertEquals
import org.joda.time.DateTime
import org.kuali.rice.kew.api.document.search.RouteNodeLookupLogic
import org.kuali.rice.kew.api.KewApiConstants

import org.junit.Before
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader

import org.kuali.rice.core.framework.resourceloader.SimpleServiceLocator
import org.kuali.rice.core.api.CoreConstants
import javax.xml.namespace.QName
import org.kuali.rice.core.framework.config.property.SimpleConfig
import org.kuali.rice.core.api.config.property.ConfigContext
import org.kuali.rice.core.impl.datetime.DateTimeServiceImpl
import org.kuali.rice.core.framework.resourceloader.BaseResourceLoader
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria
import org.kuali.rice.krad.util.KRADConstants
import org.junit.Ignore

/**
 *
 */
class DocumentSearchCriteriaTranslatorTest {
    def documentSearchCriteriaTranslator = new DocumentSearchCriteriaTranslatorImpl()

    @Before
    void init() {
        def cfg = new SimpleConfig()
        // :( these should probably just be defaulted in the impl
        cfg.putProperty("STRING_TO_DATE_FORMATS", "MM/dd/yyyy hh:mm a;MM/dd/yy;MM/dd/yyyy;MM-dd-yy;MM-dd-yyyy;MMddyy;MMMM dd;yyyy;MM/dd/yy HH:mm:ss;MM/dd/yyyy HH:mm:ss;MM-dd-yy HH:mm:ss;MMddyy HH:mm:ss;MMMM dd HH:mm:ss;yyyy HH:mm:ss")
        cfg.putProperty("STRING_TO_TIMESTAMP_FORMATS", "MM/dd/yyyy hh:mm a;MM/dd/yy;MM/dd/yyyy;MM-dd-yy;MMddyy;MMMM dd;yyyy;MM/dd/yy HH:mm:ss;MM/dd/yyyy HH:mm:ss;MM-dd-yy HH:mm:ss;MMddyy HH:mm:ss;MMMM dd HH:mm:ss;yyyy HH:mm:ss")
        cfg.putProperty("DATE_TO_STRING_FORMAT_FOR_USER_INTERFACE", "MM/dd/yyyy")
        cfg.putProperty("TIMESTAMP_TO_STRING_FORMAT_FOR_USER_INTERFACE", "MM/dd/yyyy hh:mm a")
        cfg.putProperty("DATE_TO_STRING_FORMAT_FOR_FILE_NAME", "yyyyMMdd")
        cfg.putProperty("TIMESTAMP_TO_STRING_FORMAT_FOR_FILE_NAME", "yyyyMMdd-HH-mm-ss-S")

        cfg.putProperty("application.id", "mytestingtechniqueisunstoppable")

        ConfigContext.init(cfg)

        def ssl = new SimpleServiceLocator()
        def dts = new DateTimeServiceImpl()
        dts.afterPropertiesSet()
        ssl.addService(new QName(CoreConstants.Services.DATETIME_SERVICE), dts)
        GlobalResourceLoader.stop();
        GlobalResourceLoader.addResourceLoader(new BaseResourceLoader(new QName("whatever"), ssl))
        GlobalResourceLoader.start();
    }
    /**
     * Tests that the lookup form fields are property parsed into a DocumentSearchCriteria object
     */
    @Test
    void testTranslateFieldsToCriteria() {
        // form fields
        def fields = new HashMap<String, String>()
        fields.put("documentTypeName", "whatever")
        fields.put(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_STATUS_CODE,
                   [ DocumentStatus.INITIATED.code,
                     DocumentStatus.PROCESSED.code,
                     DocumentStatus.FINAL.code,
                     "category:" + DocumentStatusCategory.SUCCESSFUL.getCode(),
                     "category:" + DocumentStatusCategory.UNSUCCESSFUL.getCode()].join(','))
        // org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl.performLookup calls LookupUtils.preProcessRangeFields(lookupFormFields);
        // to pre-process range fields, resulting in values that have already been converted to expressions by the time the DSCTranslator sees them
        fields.put("dateCreated", ">=01/01/2010")
        fields.put("routeNodeLookupLogic", RouteNodeLookupLogic.EXACTLY.toString())

        // document attrs
        def docattrs = [ attr1: [ "val1" ], attr2: [ "val2" ] ] // attr3: [ "mult0", "mult1" ] ] Note: translator does not support multiple values
        docattrs.each { k, v ->
            fields.put(KewApiConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX + k, v.join(','))
        }

        def crit = documentSearchCriteriaTranslator.translateFieldsToCriteria(fields)
        assertNotNull(crit)

        assertEquals("whatever", crit.documentTypeName)
        assertEquals([ DocumentStatus.INITIATED, DocumentStatus.PROCESSED, DocumentStatus.FINAL ] as Set, crit.getDocumentStatuses() as Set)
        assertEquals([ DocumentStatusCategory.SUCCESSFUL, DocumentStatusCategory.UNSUCCESSFUL ] as Set, crit.getDocumentStatusCategories() as Set)
        assertEquals(new DateTime(2010, 1, 1, 0, 0), crit.dateCreatedFrom)
        assertEquals(RouteNodeLookupLogic.EXACTLY, crit.routeNodeLookupLogic)

        assertEquals(docattrs.size(), crit.documentAttributeValues.size())
        docattrs.each { k, v ->
            assertEquals(v as Set, crit.documentAttributeValues[k] as Set)
        }
    }

    /**
     * Tests that the saved DocumumentLookupCriteria is properly loaded into form field values
     */
    @Test
    void testTranslateCriteriaToFields() {
        def builder = DocumentSearchCriteria.Builder.create()
        builder.documentTypeName = "whatever"
        builder.documentStatuses = [ DocumentStatus.INITIATED, DocumentStatus.PROCESSED, DocumentStatus.FINAL ]
        builder.documentStatusCategories = [ DocumentStatusCategory.UNSUCCESSFUL, DocumentStatusCategory.SUCCESSFUL ]
        builder.dateApprovedFrom = new DateTime(2010, 1, 1, 0, 0)
        builder.dateApprovedTo = new DateTime(2011, 1, 1, 0, 0)
        builder.routeNodeLookupLogic = RouteNodeLookupLogic.BEFORE

        builder.documentAttributeValues = [ attr1: [ "val1" ], attr2: [ "val2" ], attr3: [ "mult0", "mult1" ] ]

        def fields = documentSearchCriteriaTranslator.translateCriteriaToFields(builder.build())

        println fields
        assertEquals("whatever", fields["documentTypeName"][0])
        assertEquals([ DocumentStatus.INITIATED.code,
                     DocumentStatus.PROCESSED.code,
                     DocumentStatus.FINAL.code,
                     "category:" + DocumentStatusCategory.SUCCESSFUL.getCode(),
                     "category:" + DocumentStatusCategory.UNSUCCESSFUL.getCode()] as Set, fields[KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_STATUS_CODE] as Set)
        assertEquals(new DateTime(2010, 1, 1, 0, 0).toString(), fields[KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + "dateApproved"][0])
        assertEquals(new DateTime(2011, 1, 1, 0, 0).toString(), fields["dateApproved"][0])
        assertEquals(RouteNodeLookupLogic.BEFORE.toString(), fields["routeNodeLookupLogic"][0])

        builder.documentAttributeValues.each { k, v ->
            assertEquals(v as Set, fields[KewApiConstants.DOCUMENT_ATTRIBUTE_FIELD_PREFIX + k] as Set)
        }
    }
}
