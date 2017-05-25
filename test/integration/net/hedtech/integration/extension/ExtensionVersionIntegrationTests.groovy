/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by sdorfmei on 5/25/17.
 */
class ExtensionVersionIntegrationTests  extends BaseIntegrationTestCase {

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
    void givenValidRead() {
        def extensionVersion = newExtensionVersion()
        save extensionVersion

        def readExtensionVersion = ExtensionVersion.get(extensionVersion.id)
        assertNotNull readExtensionVersion.id

    }

    @Test
    void givenValidSave() {
        def extensionVersion = newExtensionVersion()
        save extensionVersion
        assertNotNull extensionVersion.id

    }


    private def newExtensionVersion() {
        ExtensionDefinitionCode extensionDefinitionCode = new ExtensionDefinitionCode()
        extensionDefinitionCode.code = "code123"
        extensionDefinitionCode.description = "code123"
        save extensionDefinitionCode

        def extensionVersion = new ExtensionVersion(
                resourceName: "baseline",
                extensionCode: "code123",
                known: "test",
                alias: "alias",
                lastModified: new Date(),
                lastModifiedBy: "test",
                dataOrigin: "Banner"
        )
        return extensionVersion
    }
}
