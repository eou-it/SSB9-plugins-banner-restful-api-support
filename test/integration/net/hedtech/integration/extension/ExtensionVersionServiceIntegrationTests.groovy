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
class ExtensionVersionServiceIntegrationTests extends BaseIntegrationTestCase {

    def extensionVersionService

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

        def readExtensionVersion = extensionVersionService.findByResourceNameAndKnownMediaType("baseline","test")
        assertNotNull readExtensionVersion
        assertNotNull readExtensionVersion.id

    }


    private def newExtensionVersion() {
        ExtensionDefinitionCode extensionDefinitionCode = new ExtensionDefinitionCode()
        extensionDefinitionCode.code = "code123"
        extensionDefinitionCode.description = "code123"
        save extensionDefinitionCode

        def extensionVersion = new ExtensionVersion(
                resourceName: "baseline",
                extensionCode: "code123",
                knownMediaType: "test",
                lastModified: new Date(),
                lastModifiedBy: "test",
                dataOrigin: "Banner"
        )
        return extensionVersion
    }
}

