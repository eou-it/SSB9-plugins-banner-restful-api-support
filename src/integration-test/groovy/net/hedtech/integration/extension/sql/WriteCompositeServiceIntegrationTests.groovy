/*******************************************************************************
 Copyright 2017-2020 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

package net.hedtech.integration.extension.sql

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import net.hedtech.banner.testing.BaseIntegrationTestCase
import net.hedtech.integration.extension.ExtensionDefinition
import net.hedtech.integration.extension.ExtractedExtensionProperty
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.text.SimpleDateFormat

/**
 * Created by sdorfmei on 5/16/17.
 */
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import static groovy.test.GroovyAssert.* 
 @ Rollback
@ Integration
 class WriteCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def writeCompositeService
    def extractedExtensionPropertyGroupBuilderService

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
    void givenOne() {

        //Get a GUID by looking at GORGUID and grabbing one (support for every developers GUIDs)
        def buildingGuidList = []
        def guidQuery = "SELECT * FROM (select gorguid_guid from gorguid where gorguid_ldm_name = 'buildings') gorguid WHERE rownum <= 1 ORDER BY rownum"
        def sqlQuery = sessionFactory.currentSession.createSQLQuery(guidQuery)
        def guidResults = sqlQuery.list()
        assertEquals 1, guidResults.size()
        guidResults.each { row ->
            buildingGuidList.add(row)
        }

        writeCompositeService.write(buildingGuidList[0], "POST", newExtractedExtensionPropertyGroups())

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
            assertEquals 200, row[0].toInteger()
            assertEquals 'McDonalds', row[1]
            assertEquals 150, row[2].toInteger()
            assertEquals "2003-06-29", new SimpleDateFormat("yyyy-MM-dd").format(row[3])
        }
    }



    private def newExtractedExtensionPropertyGroups() {
        def extractedExtensionPropertyList = []

        def extractedExtensionProperty = new ExtractedExtensionProperty()
        extractedExtensionProperty.value = 200
        extractedExtensionProperty.extensionDefinition = new ExtensionDefinition(
                extensionCode: "baseline",
                resourceName: "buildings",
                description: "Test data",
                jsonPath: "/",
                jsonLabel: "capacity",
                jsonPropertyType: "N",
                sqlProcessCode: "HEDM_EXTENSIONS",
                sqlWriteRuleCode: "BUILDINGS_WRITE",
                columnName: "SLBBLDG_MAXIMUM_CAPACITY"
        )
        extractedExtensionPropertyList.add(extractedExtensionProperty)

        extractedExtensionProperty = new ExtractedExtensionProperty()
        extractedExtensionProperty.value = "McDonalds"
        extractedExtensionProperty.extensionDefinition = new ExtensionDefinition(
                extensionCode: "baseline",
                resourceName: "buildings",
                description: "Test data",
                jsonPath: "/",
                jsonLabel: "landmark",
                jsonPropertyType: "S",
                sqlProcessCode: "HEDM_EXTENSIONS",
                sqlWriteRuleCode: "BUILDINGS_WRITE",
                columnName: "HEDM_BLDG_LANDMARK"
        )
        extractedExtensionPropertyList.add(extractedExtensionProperty)

        extractedExtensionProperty = new ExtractedExtensionProperty()
        extractedExtensionProperty.value = 150
        extractedExtensionProperty.extensionDefinition = new ExtensionDefinition(
                extensionCode: "baseline",
                resourceName: "buildings",
                description: "Test data",
                jsonPath: "/",
                jsonLabel: "roomCount",
                jsonPropertyType: "N",
                sqlProcessCode: "HEDM_EXTENSIONS",
                sqlWriteRuleCode: "BUILDINGS_WRITE",
                columnName: "HEDM_BLDG_ROOM_COUNT",
        )
        extractedExtensionPropertyList.add(extractedExtensionProperty)

        extractedExtensionProperty = new ExtractedExtensionProperty()
        extractedExtensionProperty.value = new SimpleDateFormat("yyyy-MM-dd").parse("2003-06-29")
        extractedExtensionProperty.extensionDefinition = new ExtensionDefinition(
                extensionCode: "baseline",
                resourceName: "buildings",
                description: "Test data",
                jsonPath: "/",
                jsonLabel: "constructionDate",
                jsonPropertyType: "D",
                sqlProcessCode: "HEDM_EXTENSIONS",
                sqlWriteRuleCode: "BUILDINGS_WRITE",
                columnName: "HEDM_BLDG_CONSTR_DATE"
        )
        extractedExtensionPropertyList.add(extractedExtensionProperty)

        return extractedExtensionPropertyGroupBuilderService.build(extractedExtensionPropertyList)
    }
}
