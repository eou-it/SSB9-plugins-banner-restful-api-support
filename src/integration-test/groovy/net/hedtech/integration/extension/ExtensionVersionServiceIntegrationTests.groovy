/*******************************************************************************
 Copyright 2017-2020 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by sdorfmei on 5/25/17.
 */
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import static groovy.test.GroovyAssert.* 
 @ Rollback
@ Integration
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

    @Test
    void givenValidCount() {
        def extensionVersion = newExtensionVersion()
        save extensionVersion

        def count = extensionVersionService.count()
        assertNotNull count
        assertTrue count >= 1

    }

    @Test
    void givenValidList() {
        def extensionVersion = newExtensionVersion()
        save extensionVersion

        def resultList = extensionVersionService.list()
        assertNotNull resultList
        assertTrue resultList.size >= 1

    }

    private def newExtensionVersion() {
        ExtensionDefinitionCode extensionDefinitionCode = new ExtensionDefinitionCode()
        extensionDefinitionCode.code = "code123"
        extensionDefinitionCode.description = "code123"
        save extensionDefinitionCode

        def extensionVersion = new ExtensionVersion(
                extensionCode: "code123",
                resourceName: "baseline",
                knownMediaType: "test",
                lastModified: new Date(),
                lastModifiedBy: "test",
                dataOrigin: "Banner"
        )
        return extensionVersion
    }
}

