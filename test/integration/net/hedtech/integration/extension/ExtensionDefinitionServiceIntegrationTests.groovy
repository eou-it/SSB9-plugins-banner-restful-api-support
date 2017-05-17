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
        def extensionDefinition1 = newExensionDefintion()
        save extensionDefinition1

        def extensionDefinition2 = newExensionDefintion()
        extensionDefinition2.jsonLabel = "abc456"
        save extensionDefinition2

        def extensionDefinition3 = newExensionDefintion()
        extensionDefinition3.resourceName = "def456"
        extensionDefinition3.jsonLabel = "abc789"
        save extensionDefinition3

        def extensionDefinitionList = extensionDefinitionService.findByResourceNameAndCatalog("abc123","abc123")
        assertNotNull extensionDefinitionList
        assertTrue extensionDefinitionList.size == 2

    }


    private def newExensionDefintion() {
        def extensionDefinition = new ExtensionDefinition(
                extensionType: "baseline",
                resourceName: "abc123",
                resourceCatalog: "abc123",
                description: "Test data",
                jsonPath: "/",
                jsonType: "property",
                jsonlabel: "abc123",
                version: 0,
                jsonLabel: "abc123",
                lastModified: new Date(),
                lastModifiedBy: "test",
                dataOrigin: "Banner"
        )
        return extensionDefinition
    }

}
