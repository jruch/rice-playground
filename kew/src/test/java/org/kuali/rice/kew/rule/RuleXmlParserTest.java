/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.kew.rule;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.kew.batch.KEWXmlDataLoader;
import org.kuali.rice.kew.exception.InvalidXmlException;
import org.kuali.rice.kew.exception.WorkflowServiceErrorException;
import org.kuali.rice.kew.rule.service.RuleService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.test.KEWTestCase;
import org.kuali.rice.kew.test.TestUtilities;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.xml.RuleXmlParser;
import org.springframework.test.AssertThrows;


public class RuleXmlParserTest extends KEWTestCase {
    private static final Logger LOG = Logger.getLogger(RuleXmlParserTest.class);

    protected void loadTestData() throws Exception {
        loadXmlFile("RouteTemplateConfig.xml");
        loadXmlFile("DuplicateRuleToImport.xml");
    }

    @Test public void testRuleXmlParserCacheUpdate() throws Exception {
        RuleService ruleService = KEWServiceLocator.getRuleService();
        int ruleSize = ruleService.fetchAllCurrentRulesForTemplateDocCombination("TestRuleTemplate", "TestDocumentType").size();

        List collections = new ArrayList();
        //ultimately it is the content of RulesToImport that determines whether or not we're
        //going to hit the rules xml parser
        InputStream xmlFile = TestUtilities.loadResource(this.getClass(), "RulesToImport.xml");
        collections.add(KEWXmlDataLoader.getFileXmlDocCollection(xmlFile, "WorkflowUnitTestTemp"));
        KEWServiceLocator.getXmlIngesterService().ingest(collections, null);

        Thread.sleep(5000);//give cache time to reload;
        int newRuleSize = ruleService.fetchAllCurrentRulesForTemplateDocCombination("TestRuleTemplate", "TestDocumentType").size();
        assertEquals("Three more rules should have been returned from the cached service", ruleSize + 3, newRuleSize);
    }

    @Test public void testDuplicateRule() throws IOException, InvalidXmlException {
        InputStream stream = getClass().getResourceAsStream("DuplicateRuleToImport.xml");
        assertNotNull(stream);
        log.info("Importing duplicate again");
        try {
            KEWServiceLocator.getRuleService().loadXml(stream, null);
        } catch (WorkflowServiceErrorException wsee) {
            assertNotNull(TestUtilities.findExceptionInStack(wsee, InvalidXmlException.class));
        }
    }

    @Test public void testDuplicateRuleWithExpression() throws IOException, InvalidXmlException {
        InputStream stream = getClass().getResourceAsStream("DuplicateRuleToImportWithExpression.xml");
        assertNotNull(stream);
        log.info("Importing duplicate again");
        try {
            KEWServiceLocator.getRuleService().loadXml(stream, null);
        } catch (WorkflowServiceErrorException wsee) {
            assertNotNull(TestUtilities.findExceptionInStack(wsee, InvalidXmlException.class));
        }
    }

    @Test public void testNotDuplicateRule() throws IOException, InvalidXmlException {
        InputStream stream = getClass().getResourceAsStream("NotADuplicateRuleToImport.xml");
        assertNotNull(stream);
        log.info("Importing a unique rule");
        // load the unique template first
        KEWServiceLocator.getRuleTemplateService().loadXml(stream, null);
        stream = getClass().getResourceAsStream("NotADuplicateRuleToImport.xml");
        // then the rule
        KEWServiceLocator.getRuleService().loadXml(stream, null);
    }

    @Test public void testNotDuplicateRuleWithExpression() throws IOException, InvalidXmlException {
        InputStream stream = getClass().getResourceAsStream("NotADuplicateRuleToImportWithExpression.xml");
        assertNotNull(stream);
        log.info("Importing a unique rule");
        // load the unique template first
        KEWServiceLocator.getRuleTemplateService().loadXml(stream, null);
        stream = getClass().getResourceAsStream("NotADuplicateRuleToImportWithExpression.xml");
        // then the rule
        KEWServiceLocator.getRuleService().loadXml(stream, null);
    }

    private static RuleExtensionValue getExtensionValue(List<RuleExtensionValue> list, String name) {
        for (RuleExtensionValue extensionValue: list) {
            if (name.equals(extensionValue.getKey())) return extensionValue;
        }
        return null;
    }

    @Test public void testNamedRule() {
        loadXmlFile("NamedRule.xml");
        RuleService ruleService = KEWServiceLocator.getRuleService();
        RuleBaseValues rule = ruleService.getRuleByName("ANamedRule");
        assertNotNull(rule);
        assertEquals("ANamedRule", rule.getName());
        assertEquals("A named rule", rule.getDescription());
        LOG.info("Before Testing  To and From Dates : " + rule.getToDateString()+" "+rule.getFromDateString());
        assertNull(rule.getToDateString());
        assertNull(rule.getFromDateString());
        LOG.info("Rule To and From Dates : " + rule.getDocTypeName()+" "+rule.getName());
        List extensions = rule.getRuleExtensions();
        assertEquals(1, extensions.size());
        RuleExtension extension = (RuleExtension) extensions.get(0);
        assertEquals("TestRuleAttribute", extension.getRuleTemplateAttribute().getRuleAttribute().getName());
        List extensionValues = extension.getExtensionValues();
        assertEquals(2, extensionValues.size());
        //RuleExtensionValue extensionValue = (RuleExtensionValue) extensionValues.get(0);
        RuleExtensionValue extensionValue = getExtensionValue(extensionValues, "color");
        assertEquals("color", extensionValue.getKey());
        assertEquals("green", extensionValue.getValue());
        //extensionValue = (RuleExtensionValue) extensionValues.get(1);
        extensionValue = getExtensionValue(extensionValues, "shape");
        assertEquals("shape", extensionValue.getKey());
        assertEquals("square", extensionValue.getValue());
        List responsibilities = rule.getResponsibilities();
        assertEquals(1, responsibilities.size());
        RuleResponsibility responsibility = (RuleResponsibility) responsibilities.get(0);
        assertEquals("user1", responsibility.getPrincipal().getPrincipalName());
        assertEquals("A", responsibility.getActionRequestedCd());
    }

    @Test public void testNamedRuleWithExpression() {
        loadXmlFile("NamedRuleWithExpression.xml");
        RuleService ruleService = KEWServiceLocator.getRuleService();
        RuleBaseValues rule = ruleService.getRuleByName("ANamedRule");
        assertNotNull(rule);
        assertEquals("ANamedRule", rule.getName());
        assertEquals("A named rule", rule.getDescription());
        List extensions = rule.getRuleExtensions();
        assertEquals(1, extensions.size());
        RuleExtension extension = (RuleExtension) extensions.get(0);
        assertEquals("TestRuleAttribute", extension.getRuleTemplateAttribute().getRuleAttribute().getName());
        List extensionValues = extension.getExtensionValues();
        assertEquals(2, extensionValues.size());
        //RuleExtensionValue extensionValue = (RuleExtensionValue) extensionValues.get(0);
        RuleExtensionValue extensionValue = getExtensionValue(extensionValues, "color");
        assertEquals("color", extensionValue.getKey());
        assertEquals("green", extensionValue.getValue());
        //extensionValue = (RuleExtensionValue) extensionValues.get(1);
        extensionValue = getExtensionValue(extensionValues, "shape");
        assertEquals("shape", extensionValue.getKey());
        assertEquals("square", extensionValue.getValue());
        List responsibilities = rule.getResponsibilities();
        assertEquals(1, responsibilities.size());
        RuleResponsibility responsibility = (RuleResponsibility) responsibilities.get(0);
        assertEquals("user1", responsibility.getPrincipal().getPrincipalName());
        assertEquals("A", responsibility.getActionRequestedCd());
        assertNotNull(rule.getRuleExpressionDef());
        assertEquals("someType", rule.getRuleExpressionDef().getType());
        assertEquals("some expression", rule.getRuleExpressionDef().getExpression());
    }

    @Test public void testUpdatedRule() {
        testNamedRule();
        
        RuleService ruleService = KEWServiceLocator.getRuleService();
        // let's grab the responsibility id from the original named rule
        RuleBaseValues rule = ruleService.getRuleByName("ANamedRule");
        Long responsibilityId = rule.getResponsibilities().get(0).getResponsibilityId();
        Long ruleId = rule.getRuleBaseValuesId();
        Integer versionNumber = rule.getVersionNbr();
        
        loadXmlFile("UpdatedNamedRule.xml");
        rule = ruleService.getRuleByName("ANamedRule");
        assertNotNull(rule);
        assertEquals("ANamedRule", rule.getName());
        assertTrue("Rule should be current.", rule.getCurrentInd());
        assertFalse("Rule should not be a delegate rule.", rule.getDelegateRule());
        assertFalse("Rule should not be a template rule.", rule.getTemplateRuleInd());
        assertNull("Rule should not have a from date.", rule.getFromDate());
        assertNull("Rule should not have a to date.", rule.getToDate());
        
        // check that the previous versions line up and that the rule is not the same
        assertFalse("Rule ids should be different", ruleId.equals(rule.getRuleBaseValuesId()));
        assertEquals("Previous version id should be correct", ruleId, rule.getPreviousVersionId());
        assertEquals("Version ids are incorrect", new Integer(versionNumber + 1), rule.getVersionNbr());
        // fetch the original rule and verify that it's no longer current
        RuleBaseValues oldRule = ruleService.findRuleBaseValuesById(ruleId);
        assertFalse("Old rule should no longer be current.", oldRule.getCurrentInd());
        
        assertEquals("A named rule with an updated description, rule extension values, and responsibilities", rule.getDescription());
        List extensions = rule.getRuleExtensions();
        assertEquals(1, extensions.size());
        RuleExtension extension = (RuleExtension) extensions.get(0);
        assertEquals("TestRuleAttribute", extension.getRuleTemplateAttribute().getRuleAttribute().getName());
        List extensionValues = extension.getExtensionValues();
        assertEquals(2, extensionValues.size());
        //RuleExtensionValue extensionValue = (RuleExtensionValue) extensionValues.get(0);
        RuleExtensionValue extensionValue = getExtensionValue(extensionValues, "flavor");
        assertEquals("flavor", extensionValue.getKey());
        assertEquals("vanilla", extensionValue.getValue());
        //extensionValue = (RuleExtensionValue) extensionValues.get(1);
        extensionValue = getExtensionValue(extensionValues, "value");
        assertEquals("value", extensionValue.getKey());
        assertEquals("10", extensionValue.getValue());
        List responsibilities = rule.getResponsibilities();
        assertEquals(2, responsibilities.size());
        
        // responsibility should have the same id as our original responsibility
        RuleResponsibility responsibility = (RuleResponsibility) responsibilities.get(0);
        assertEquals(responsibilityId, responsibility.getResponsibilityId());
        assertEquals("user1", responsibility.getPrincipal().getPrincipalName());
        assertEquals("A", responsibility.getActionRequestedCd());
        assertEquals(new Integer(1), responsibility.getPriority());
        
        responsibility = (RuleResponsibility) responsibilities.get(1);
        assertFalse(responsibilityId.equals(responsibility.getResponsibilityId()));
        assertEquals("user2", responsibility.getPrincipal().getPrincipalName());
        assertEquals("F", responsibility.getActionRequestedCd());
        assertEquals(new Integer(1), responsibility.getPriority());
    }

    @Test public void testUpdatedRuleWithExpression() {
        testNamedRule();
        loadXmlFile("UpdatedNamedRuleWithExpression.xml");
        RuleService ruleService = KEWServiceLocator.getRuleService();
        RuleBaseValues rule = ruleService.getRuleByName("ANamedRule");
        assertNotNull(rule);
        assertEquals("ANamedRule", rule.getName());
        assertEquals("A named rule with an updated description, rule extension values, and responsibilities", rule.getDescription());
        List extensions = rule.getRuleExtensions();
        assertEquals(1, extensions.size());
        RuleExtension extension = (RuleExtension) extensions.get(0);
        assertEquals("TestRuleAttribute", extension.getRuleTemplateAttribute().getRuleAttribute().getName());
        List extensionValues = extension.getExtensionValues();
        assertEquals(2, extensionValues.size());
        //RuleExtensionValue extensionValue = (RuleExtensionValue) extensionValues.get(0);
        RuleExtensionValue extensionValue = getExtensionValue(extensionValues, "flavor");
        assertEquals("flavor", extensionValue.getKey());
        assertEquals("vanilla", extensionValue.getValue());
        //extensionValue = (RuleExtensionValue) extensionValues.get(1);
        extensionValue = getExtensionValue(extensionValues, "value");
        assertEquals("value", extensionValue.getKey());
        assertEquals("10", extensionValue.getValue());
        List responsibilities = rule.getResponsibilities();
        assertEquals(1, responsibilities.size());
        RuleResponsibility responsibility = (RuleResponsibility) responsibilities.get(0);
        assertEquals("user2", responsibility.getPrincipal().getPrincipalName());
        assertEquals("F", responsibility.getActionRequestedCd());
    }

    /**
     * This test tests that an anonymous rule will still be checked against named rules for duplication.
     */
    @Test public void testAnonymousDuplicatesNamed() {
        testNamedRule();

        final InputStream stream = getClass().getResourceAsStream("DuplicateAnonymousRule.xml");
        assertNotNull(stream);
        log.info("Importing anonymous duplicate rule");
        AssertThrows at = new AssertThrows(WorkflowServiceErrorException.class, "Expected exception was not thrown") {
            @Override
            public void test() {
                KEWServiceLocator.getRuleService().loadXml(stream, null);
            }
        };
        at.runTest();
        assertNotNull("Expected exception was not thrown", TestUtilities.findExceptionInStack(at.getActualException(), InvalidXmlException.class));
    }

    /**
     * This test tests that an anonymous rule will still be checked against named rules for duplication.
     */
    @Test public void testAnonymousWithExpressionDuplicatesNamed() {
        testNamedRuleWithExpression();

        final InputStream stream = getClass().getResourceAsStream("DuplicateAnonymousRuleWithExpression.xml");
        assertNotNull(stream);
        log.info("Importing anonymous duplicate rule");
        AssertThrows at = new AssertThrows(WorkflowServiceErrorException.class, "Expected exception was not thrown") {
            @Override
            public void test() {
                KEWServiceLocator.getRuleService().loadXml(stream, null);
            }
        };
        at.runTest();
        assertNotNull("Expected exception was not thrown", TestUtilities.findExceptionInStack(at.getActualException(), InvalidXmlException.class));
    }

    @Test public void testParameterReplacement() throws IOException, InvalidXmlException {
        ConfigContext.getCurrentContextConfig().putProperty("test.replacement.user", "user3");
        ConfigContext.getCurrentContextConfig().putProperty("test.replacement.workgroup", "WorkflowAdmin");
        List<RuleBaseValues> rules = new RuleXmlParser().parseRules(getClass().getResourceAsStream("ParameterizedRule.xml"));
        assertEquals(1, rules.size());
        RuleBaseValues rule = rules.get(0);
        assertEquals(2, rule.getResponsibilities().size());
        RuleResponsibility resp = (RuleResponsibility) rule.getResponsibilities().get(0);

        if (resp.isUsingWorkflowUser()) {
            assertEquals("user3", resp.getPrincipal().getPrincipalName());
        } else {
            assertEquals("WorkflowAdmin", resp.getGroup().getGroupName());
        }

        ConfigContext.getCurrentContextConfig().putProperty("test.replacement.user", "user1");
        ConfigContext.getCurrentContextConfig().putProperty("test.replacement.workgroup", "TestWorkgroup");
        rules = new RuleXmlParser().parseRules(getClass().getResourceAsStream("ParameterizedRule.xml"));
        assertEquals(1, rules.size());
        rule = rules.get(0);
        assertEquals(2, rule.getResponsibilities().size());
        resp = (RuleResponsibility) rule.getResponsibilities().get(0);

        if (resp.isUsingWorkflowUser()) 
        {
            assertEquals("user1", resp.getPrincipal().getPrincipalName());   
        } 
        else 
        {
            assertEquals("TestWorkgroup", resp.getGroup().getGroupName());
        }
    }

    @Test public void removeTemplateFromNamedRule() {
        RuleService ruleService = KEWServiceLocator.getRuleService();
        int originalRuleCount = ruleService.fetchAllCurrentRulesForTemplateDocCombination("TestRuleTemplate", "TestDocumentType").size();

        testNamedRule();

        LOG.debug("Rules for doctype/template combo:");
        int ruleCount = 0;
        List<RuleBaseValues> list = ruleService.fetchAllCurrentRulesForTemplateDocCombination("TestRuleTemplate", "TestDocumentType");
        if (list != null) {
            ruleCount = list.size();
            for (RuleBaseValues rbv: list) {
                LOG.info(rbv);
            }
        }

        loadXmlFile("NamedRuleWithoutTemplate.xml");

        LOG.debug("Rules for doctype/template combo after import of named rule:");
        int ruleCountAfter = 0;
        list = ruleService.fetchAllCurrentRulesForTemplateDocCombination("TestRuleTemplate", "TestDocumentType");
        if (list != null) {
            ruleCountAfter = list.size();
            for (RuleBaseValues rbv: list) {
                LOG.info(rbv);
            }
        }

        RuleBaseValues rule = ruleService.getRuleByName("ANamedRule");

        assertNotNull(rule);
        LOG.info("Rule id of latest version: " + rule.getRuleBaseValuesId());
        assertEquals("ANamedRule", rule.getName());
        assertEquals("A named rule with previously defined template removed", rule.getDescription());

        assertEquals("The rules for template/doctype combo should have been decreased by one after reimport of named rule without template", ruleCount - 1, ruleCountAfter);
        assertEquals("Rule count should be original template/doctype combo rule count after removing template from named rule", originalRuleCount, ruleCountAfter);

        assertNull(rule.getRuleTemplate());

        // templateless rules cannot have extensions, so these should be removed
        List extensions = rule.getRuleExtensions();
        assertEquals(0, extensions.size());

        List responsibilities = rule.getResponsibilities();
        assertEquals(1, responsibilities.size());
        RuleResponsibility responsibility = (RuleResponsibility) responsibilities.get(0);
        assertEquals("user2", responsibility.getPrincipal().getPrincipalName());
        assertEquals("F", responsibility.getActionRequestedCd());
    }

    @Test public void testInvalidTemplatelessNamedRule() {
        testNamedRule();
        try {
        	loadXmlFile("InvalidTemplatelessNamedRule.xml");
        	fail("Rule should have failed to load because it attempts to define extensions on a templateless rule.");
        } catch (Exception e) {}
    }
    
    @Test public void testRulesWithDifferentResponsibilityTypes() throws Exception {
    	loadXmlFile("RulesWithDifferentResponsibilityTypes.xml");
    	RuleService ruleService = KEWServiceLocator.getRuleService();
    	
    	RuleBaseValues rule = ruleService.getRuleByName("RespTypeTest1");
    	assertNotNull(rule);
    	assertEquals("Rule should have a principal responsibility", KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID, rule.getResponsibilities().get(0).getRuleResponsibilityType());
    	assertEquals("Rule should have a principal id of user1", "user1", rule.getResponsibilities().get(0).getRuleResponsibilityName());
    	
    	rule = ruleService.getRuleByName("RespTypeTest2");
    	assertNotNull(rule);
    	assertEquals("Rule should have a principal responsibility", KEWConstants.RULE_RESPONSIBILITY_WORKFLOW_ID, rule.getResponsibilities().get(0).getRuleResponsibilityType());
    	assertEquals("Rule should have a principal id of user1", "user1", rule.getResponsibilities().get(0).getRuleResponsibilityName());
    	
    	rule = ruleService.getRuleByName("RespTypeTest3");
    	assertNotNull(rule);
    	assertEquals("Rule should have a group responsibility", KEWConstants.RULE_RESPONSIBILITY_GROUP_ID, rule.getResponsibilities().get(0).getRuleResponsibilityType());
    	assertEquals("Rule should have a group id of 3001", "3001", rule.getResponsibilities().get(0).getRuleResponsibilityName());

    	rule = ruleService.getRuleByName("RespTypeTest4");
    	assertNotNull(rule);
    	assertEquals("Rule should have a group responsibility", KEWConstants.RULE_RESPONSIBILITY_GROUP_ID, rule.getResponsibilities().get(0).getRuleResponsibilityType());
    	assertEquals("Rule should have a group id of 1", "1", rule.getResponsibilities().get(0).getRuleResponsibilityName());

    	rule = ruleService.getRuleByName("RespTypeTest5");
    	assertNotNull(rule);
    	assertEquals("Rule should have a role responsibility", KEWConstants.RULE_RESPONSIBILITY_ROLE_ID, rule.getResponsibilities().get(0).getRuleResponsibilityType());
    	assertEquals("Invalid role name", "org.kuali.rice.kew.rule.TestRuleAttribute!TEST", rule.getResponsibilities().get(0).getRuleResponsibilityName());

    	rule = ruleService.getRuleByName("RespTypeTest6");
    	assertNotNull(rule);
    	assertEquals("Rule should have a role responsibility", KEWConstants.RULE_RESPONSIBILITY_ROLE_ID, rule.getResponsibilities().get(0).getRuleResponsibilityType());
    	assertEquals("Invalid role name", "org.kuali.rice.kew.rule.TestRuleAttribute!TEST", rule.getResponsibilities().get(0).getRuleResponsibilityName());
    }
}