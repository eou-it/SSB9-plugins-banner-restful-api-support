package net.hedtech.integration.extension.sql

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import net.hedtech.banner.testing.BaseIntegrationTestCase
import net.hedtech.integration.extension.ExtensionDefinition
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by sdorfmei on 5/16/17.
 */
class ReadCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def readCompositeService

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
    void givenMany() {
        Map requestParms

        def resources = '''
                [{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce"},
                 {"id": "26a2673f-9bc6-4649-a3e8-213d0ff4afbd"}
          
                ]'''
        def extensionDefinition1 = newExensionDefinition()
        save extensionDefinition1
        def resultList = readCompositeService.read([extensionDefinition1],requestParms,resources)

        assertNotNull resultList
    }

    @Test
    void givenOne() {
        Map requestParms = [id: '24c47f0a-0eb7-48a3-85a6-2c585691c6ce']

        def resources = '''
                {"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''

        def extensionDefinition1 = newExensionDefinition()
        save extensionDefinition1
        def resultList = readCompositeService.read([extensionDefinition1],requestParms,resources)

        assertNotNull resultList
    }

*/

    private def newExensionDefinition() {
        def extensionDefinition = new ExtensionDefinition(
                extensionType: "baseline",
                resourceName: "buildings",
                resourceCatalog: "abc123",
                description: "Test data",
                jsonPath: "/",
                jsonType: "property",
                jsonlabel: "maxcapacity",
                selectColumnName: "SLBBLDG_MAXIMUM_CAPACITY",
                sqlRuleCode: "1232",
                sqlProcessCode: "abc",
                version: 0,
                jsonLabel: "abc123",
                lastModified: new Date(),
                lastModifiedBy: "test",
                dataOrigin: "Banner"
        )
        return extensionDefinition
    }
}
