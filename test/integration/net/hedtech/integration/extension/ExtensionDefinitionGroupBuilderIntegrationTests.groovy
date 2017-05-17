/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
/**
 * Created by sdorfmei on 5/17/17.
 */
class ExtensionDefinitionGroupBuilderIntegrationTests extends BaseIntegrationTestCase {

    def extensionDefinitionGroupBuilderService

    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    public void tearDown() {
        super.tearDown()
    }

    @Test
    void givenNullParamExpectNull() {
        def result = extensionDefinitionGroupBuilderService.build(null)
        assertNull result
    }

    @Test
    void givenOneValueExpectOneGroup() {
        ExtensionDefinition extensionDefinition = new ExtensionDefinition()
        extensionDefinition.sqlRuleCode = "abc"
        extensionDefinition.sqlProcessCode = "123"
        extensionDefinition.resourceName="foo"

        def extensionDefintionList = []
        extensionDefintionList.add(extensionDefinition)

        def result = extensionDefinitionGroupBuilderService.build(extensionDefintionList)
        assertNotNull result
        assertTrue result.size == 1

    }

    @Test
    void givenThreeValuesExpectOneGroup() {

        ExtensionDefinition extensionDefinition = new ExtensionDefinition()
        extensionDefinition.sqlRuleCode = "abc"
        extensionDefinition.sqlProcessCode = "123"
        extensionDefinition.resourceName="foo"
        extensionDefinition.selectColumnName = "bar"

        ExtensionDefinition extensionDefinition2 = new ExtensionDefinition()
        extensionDefinition2.sqlRuleCode = "abc"
        extensionDefinition2.sqlProcessCode = "123"
        extensionDefinition2.resourceName="foo"
        extensionDefinition2.selectColumnName = "bar2"

        ExtensionDefinition extensionDefinition3 = new ExtensionDefinition()
        extensionDefinition3.sqlRuleCode = "abc"
        extensionDefinition3.sqlProcessCode = "123"
        extensionDefinition3.resourceName="foo"
        extensionDefinition3.selectColumnName = "bar3"


        def extensionDefintionList = []
        extensionDefintionList.add(extensionDefinition)
        extensionDefintionList.add(extensionDefinition2)
        extensionDefintionList.add(extensionDefinition3)

        def result = extensionDefinitionGroupBuilderService.build(extensionDefintionList)
        assertNotNull result
        assertTrue result.size == 1


    }

    @Test
    void givenTwoGroupsExpectTwoGroups() {

        ExtensionDefinition extensionDefinition = new ExtensionDefinition()
        extensionDefinition.sqlRuleCode = "abc"
        extensionDefinition.sqlProcessCode = "123"
        extensionDefinition.resourceName="foo"
        extensionDefinition.selectColumnName = "bar"

        ExtensionDefinition extensionDefinition2 = new ExtensionDefinition()
        extensionDefinition2.sqlRuleCode = "abc"
        extensionDefinition2.sqlProcessCode = "123"
        extensionDefinition2.resourceName="foo"
        extensionDefinition2.selectColumnName = "bar2"


        //Have one that has a different rule
        ExtensionDefinition extensionDefinition3 = new ExtensionDefinition()
        extensionDefinition3.sqlRuleCode = "def"
        extensionDefinition3.sqlProcessCode = "123"
        extensionDefinition3.resourceName="foo"
        extensionDefinition3.selectColumnName = "bar3"


        def extensionDefintionList = []
        extensionDefintionList.add(extensionDefinition)
        extensionDefintionList.add(extensionDefinition2)
        extensionDefintionList.add(extensionDefinition3)

        def result = extensionDefinitionGroupBuilderService.build(extensionDefintionList)
        assertNotNull result
        assertTrue result.size == 2

    }
}
