/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
/**
 * Created by sdorfmei on 5/19/17.
 */
class ExtensionDefinitionCodeIntegrationTests extends BaseIntegrationTestCase {

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
        def extensionDefinitionCode = newExtensionDefinitionCode()
        save extensionDefinitionCode

        def extensionDefinitionCodeRead = ExtensionDefinitionCode.get(extensionDefinitionCode.id)
        assertNotNull extensionDefinitionCodeRead.id

    }

    @Test
    void testSave() {
        def extensionDefinitionCode = newExtensionDefinitionCode()
        save extensionDefinitionCode
        assertNotNull extensionDefinitionCode.id

    }

    private def newExtensionDefinitionCode() {
        def extensionDefinitionCode = new ExtensionDefinitionCode(
                code: "test",
                description : "test",
                lastModified: new Date(),
                lastModifiedBy: "test",
                dataOrigin: "Banner"
        )
        return extensionDefinitionCode
    }
}
