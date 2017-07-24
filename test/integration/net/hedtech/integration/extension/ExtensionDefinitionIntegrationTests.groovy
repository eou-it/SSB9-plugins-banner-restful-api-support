/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by sdorfmei on 5/15/17.
 */
class ExtensionDefinitionIntegrationTests extends BaseIntegrationTestCase {

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
    void testRead() {
        def extensionDefinition = newExtensionDefinition()
        save extensionDefinition

        def readExtensionDefinition = ExtensionDefinition.get(extensionDefinition.id)
        assertNotNull readExtensionDefinition.id

    }

    @Test
    void testSave() {
        def extensionDefinition = newExtensionDefinition()
        save extensionDefinition
        assertNotNull extensionDefinition.id

    }

    private def newExtensionDefinition() {

        ExtensionDefinitionCode extensionDefinitionCode = new ExtensionDefinitionCode()
        extensionDefinitionCode.code = "code123"
        extensionDefinitionCode.description = "code123"
        save extensionDefinitionCode


        def extensionDefinition = new ExtensionDefinition(
                extensionCode: "code123",
                resourceName: "abc123",
                description: "Test data",
                jsonPath: "/",
                jsonPropertyType: "S",
                jsonlabel: "abc123",
                columnName: "columnName",
                version: 0,
                jsonLabel: "abc123",
                lastModified: new Date(),
                lastModifiedBy: "test",
                dataOrigin: "Banner"
        )
        return extensionDefinition
    }

}
