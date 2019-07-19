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


    @Test
    void testNoExtensions() {
        def testResourceName = "foo"
        def testExtensionCode = "ETHOS_API-9.10"

        def responseContent = """{"id":"38d4154e-276d-4907-969b-62579cf1b7a6","name":"my test","desc":"my description"}"""
        def expectedContent = """{"id":"38d4154e-276d-4907-969b-62579cf1b7a6","name":"my test","desc":"my description"}"""

        ExtensionProcessResult extensionProcessResult = extensionReadCompositeService.read(testResourceName, testExtensionCode, responseContent)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content
        assertFalse extensionProcessResult.extensionsApplied
    }


    @Test
    void testGetOne() {
        def testResourceName = "buildings"
        def testExtensionCode = "ETHOS_API-9.10"

        def buildingGuidList = []
        def guidQuery = "select gorguid_guid from gorguid where gorguid_ldm_name = 'buildings' and gorguid_domain_key = 'TECH'"
        def sqlQuery = sessionFactory.currentSession.createSQLQuery(guidQuery)
        def guidResults = sqlQuery.list()
        assertEquals 1, guidResults.size()
        guidResults.each { row ->
            buildingGuidList.add(row)
        }

        def responseContent = """{"id":""" + "\"${buildingGuidList[0]}\"" + ""","code":"TECH","title":"Technology Hall"}"""
        def expectedContent = """{"id":""" + "\"${buildingGuidList[0]}\"" + ""","code":"TECH","title":"Technology Hall","hedmCapacity":150,"hedmConstructionDate":"2013-06-24","hedmLandmark":"SMALL RED TREE","hedmRoomCount":10}"""

        ExtensionProcessResult extensionProcessResult = extensionReadCompositeService.read(testResourceName, testExtensionCode, responseContent)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content
        assertTrue extensionProcessResult.extensionsApplied
    }

}
