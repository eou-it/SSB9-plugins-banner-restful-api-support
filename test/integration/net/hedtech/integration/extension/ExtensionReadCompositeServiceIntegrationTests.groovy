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
class ExtensionReadCompositeServiceIntegrationTests  extends BaseIntegrationTestCase  {

    def extensionReadCompositeService

    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    public void tearDown() {
        super.tearDown()
    }


   /* @Test
    void testList() {
        def testResourceName = "buildings"
        def testVersion = "application/vnd.hedtech.integration.v6+json"
        def resources = '''
                [{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce"},
                 {"id": "26a2673f-9bc6-4649-a3e8-213d0ff4afbd"}
          
                ]'''


       // ExtensionDefinition extensionDefinition = newExensionDefintion()
       // save extensionDefinition
        ExtensionProcessResult extensionProcessResult = extensionReadCompositeService.read(testResourceName,testVersion,
                null,null,resources)
        assertNotNull extensionProcessResult
    }*/

    @Test
    void testNoExtensions() {
        def testResourceName = "foo"
        def testVersion = "application/vnd.hedtech.integration.v6+json"

        ExtensionProcessResult extensionProcessResult = extensionReadCompositeService.read(testResourceName,testVersion,
                null,null,null)
        assertNotNull extensionProcessResult
        assertFalse extensionProcessResult.extensionsApplied
    }



    private def newExensionDefintion() {
        def extensionDefinition = new ExtensionDefinition(
                extensionType: "SQL",
                extensionCode: "CODE1",
                resourceName: "buildings",
                desc: "Test data",
                jsonPath: "/",
                jsonType: "property",
                jsonlabel: "slbbldg_maximum_capacity",
                version: 0,
                jsonLabel: "maxcapcity",
                lastModified: new Date(),
                lastModifiedBy: "test",
                dataOrigin: "Banner"
        )
        return extensionDefinition
    }

}
