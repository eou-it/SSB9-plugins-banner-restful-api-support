/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by sdorfmei on 5/18/17.
 */
class ExtensionWriteCompositeServiceIntegrationTests  extends BaseIntegrationTestCase  {

    def extensionWriteCompositeService

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
    void testNoExtensions() {
        def testResourceName = "foo"
        def testEXtensionCode = "ETHOS_API-9.9"

        ExtensionProcessResult extensionProcessResult = extensionWriteCompositeService.write(testResourceName, testEXtensionCode, null, null, null)
        assertNotNull extensionProcessResult
        assertNull extensionProcessResult.content
        assertFalse extensionProcessResult.extensionsApplied
    }


    @Test
    void testList() {
        def testResourceName = "buildings"
        def testEXtensionCode = "ETHOS_API-9.9"

        ExtensionProcessResult extensionProcessResult = extensionWriteCompositeService.write(testResourceName, testEXtensionCode, null, null, null)
        assertNotNull extensionProcessResult
        assertNull extensionProcessResult.content
        assertFalse extensionProcessResult.extensionsApplied
    }

}
