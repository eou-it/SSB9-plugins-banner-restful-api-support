/*******************************************************************************
 Copyright 2017-2020 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.text.SimpleDateFormat

/**
 * Created by sdorfmei on 5/18/17.
 */
import groovy.util.logging.Slf4j
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import static groovy.test.GroovyAssert.* 
 @ Rollback
@ Slf4j
@ Integration
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
        def testExtensionCode = "ETHOS_API-9.10"

        def responseContent = """{"id":"38d4154e-276d-4907-969b-62579cf1b7a6","name":"my test","desc":"my description"}"""
        def expectedContent = responseContent

        ExtensionProcessResult extensionProcessResult = extensionWriteCompositeService.write(testResourceName, testExtensionCode, "POST", responseContent, responseContent)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content
    }


    @Test
    void testPut() {
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

        def responseContent = """{"id":""" + "\"${buildingGuidList[0]}\"" + ""","code":"TECH","title":"Technology Hall","hedmCapacity":750,"hedmConstructionDate":"2012-03-25","hedmLandmark":"BIG RED TREE","hedmRoomCount":500}"""
        def expectedContent = responseContent

        ExtensionProcessResult extensionProcessResult = extensionWriteCompositeService.write(testResourceName, testExtensionCode, "PUT", responseContent, responseContent)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content

        def verifyQuery = '''
                select slbbldg_maximum_capacity,
                       sys.anydata.accessVarchar2(g1.gorsdav_value) as HEDM_BLDG_LANDMARK,
                       sys.anydata.accessNumber(g2.gorsdav_value) as HEDM_BLDG_ROOM_COUNT,
                       sys.anydata.accessDate(g3.gorsdav_value) as HEDM_BLDG_CONSTR_DATE
                  from gorguid g, slbbldg s, gorsdav g1, gorsdav g2, gorsdav g3
                 where gorguid_ldm_name = 'buildings'
                   and g.gorguid_domain_surrogate_id = s.slbbldg_surrogate_id
                   and g1.gorsdav_table_name(+) = 'SLBBLDG'
                   and g2.gorsdav_table_name(+) = 'SLBBLDG'
                   and g3.gorsdav_table_name(+) = 'SLBBLDG'
                   and g1.gorsdav_attr_name(+) = 'HEDM_BLDG_LANDMARK'
                   and g2.gorsdav_attr_name(+) = 'HEDM_BLDG_ROOM_COUNT'
                   and g3.gorsdav_attr_name(+) = 'HEDM_BLDG_CONSTR_DATE'
                   and g1.gorsdav_pk_parenttab(+) = s.slbbldg_bldg_code
                   and g2.gorsdav_pk_parenttab(+) = s.slbbldg_bldg_code
                   and g3.gorsdav_pk_parenttab(+) = s.slbbldg_bldg_code
                   and gorguid_guid = :GUID'''
        sqlQuery = sessionFactory.currentSession.createSQLQuery(verifyQuery)
        sqlQuery.setString("GUID", buildingGuidList[0])
        def verifyResults = sqlQuery.list()
        assertEquals 1, verifyResults.size()
        verifyResults.each { row ->
            assertEquals 750, row[0].toInteger()
            assertEquals 'BIG RED TREE', row[1]
            assertEquals 500, row[2].toInteger()
            assertEquals "2012-03-25", new SimpleDateFormat("yyyy-MM-dd").format(row[3])
        }
    }

}
