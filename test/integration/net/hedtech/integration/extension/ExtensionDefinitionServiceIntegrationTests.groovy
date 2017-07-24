/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ExtensionDefinitionServiceIntegrationTests extends BaseIntegrationTestCase {

    def extensionDefinitionService

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
    void testFindByResourceNameAndCatalog() {

        ExtensionDefinitionCode extensionDefinitionCode = newExtensionDefinitionCode()

        def extensionDefinition1 = newExtensionDefinition(extensionDefinitionCode)
        save extensionDefinition1

        def extensionDefinition2 = newExtensionDefinition(extensionDefinitionCode)
        extensionDefinition2.jsonLabel = "abc456"
        save extensionDefinition2

        def extensionDefinition3 = newExtensionDefinition(extensionDefinitionCode)
        extensionDefinition3.resourceName = "def456"
        extensionDefinition3.jsonLabel = "abc789"
        save extensionDefinition3

        def extensionDefinitionList = extensionDefinitionService.findAllByResourceNameAndExtensionCode("abc123",extensionDefinitionCode.code)
        assertNotNull extensionDefinitionList
        assertTrue extensionDefinitionList.size == 2

    }


    private def newExtensionDefinition(ExtensionDefinitionCode extensionDefinitionCode) {
        def extensionDefinition = new ExtensionDefinition(
                extensionCode: extensionDefinitionCode.code,
                resourceName: "abc123",
                description: "Test data",
                jsonPath: "/",
                jsonPropertyType: "S",
                jsonlabel: "abc123",
                version: 0,
                jsonLabel: "abc123",
                lastModified: new Date(),
                lastModifiedBy: "test",
                dataOrigin: "Banner"
        )
        return extensionDefinition
    }


    private def newExtensionDefinitionCode() {
        ExtensionDefinitionCode extensionDefinitionCode = new ExtensionDefinitionCode()
        extensionDefinitionCode.code = "code123"
        extensionDefinitionCode.description = "code123"

        save extensionDefinitionCode
        return extensionDefinitionCode
    }

}
