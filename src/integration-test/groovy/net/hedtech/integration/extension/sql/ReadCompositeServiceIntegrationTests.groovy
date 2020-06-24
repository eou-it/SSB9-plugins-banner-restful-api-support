/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension.sql

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import net.hedtech.banner.testing.BaseIntegrationTestCase
import net.hedtech.integration.extension.ExtensionDefinition
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
@Rollback
@Integration
 class ReadCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def readCompositeService
    def extensionDefinitionSourceGroupBuilderService

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
    void givenOneSet() {

        //Get a GUID by looking at GORGUID and grabbing one (support for every developers GUIDs)
        def buildingGuidList = []
        def guidQuery = "select gorguid_guid from gorguid where gorguid_ldm_name = 'buildings' and gorguid_domain_key = 'BIOL'"
        def sqlQuery = sessionFactory.currentSession.createSQLQuery(guidQuery)
        def guidResults = sqlQuery.list()
        assertEquals 1, guidResults.size()
        guidResults.each { row ->
            buildingGuidList.add(row)
        }

        def resultList = readCompositeService.read(newExensionDefinitionGroupsFirstSet(),buildingGuidList)
        assertNotNull resultList
        assertEquals 1, resultList.size()
        resultList.each { extensionProcessReadResult ->
            assertEquals buildingGuidList[0], extensionProcessReadResult.resourceId
            assertEquals "/", extensionProcessReadResult.jsonPath
            assertEquals "maxcapacity", extensionProcessReadResult.jsonLabel
            assertEquals "N", extensionProcessReadResult.jsonPropertyType
            assertTrue extensionProcessReadResult.value instanceof BigDecimal
            assertEquals 50000, extensionProcessReadResult.value.toInteger()
        }
    }


    @Test
    void givenBothSets() {

        //Get a GUID by looking at GORGUID and grabbing one (support for every developers GUIDs)
        def buildingGuidList = []
        def guidQuery = "select gorguid_guid from gorguid where gorguid_ldm_name = 'buildings' and gorguid_domain_key = 'TECH'"
        def sqlQuery = sessionFactory.currentSession.createSQLQuery(guidQuery)
        def guidResults = sqlQuery.list()
        assertEquals 1, guidResults.size()
        guidResults.each { row ->
            buildingGuidList.add(row)
        }

        def resultList = readCompositeService.read(newExensionDefinitionGroupsBothSets(),buildingGuidList)
        assertNotNull resultList
        assertEquals 4, resultList.size()
        def foundCount = 0
        resultList.each { extensionProcessReadResult ->
            assertEquals buildingGuidList[0], extensionProcessReadResult.resourceId
            assertEquals "/", extensionProcessReadResult.jsonPath
            if (extensionProcessReadResult.jsonLabel == "maxcapacity") {
                foundCount++
                assertEquals "N", extensionProcessReadResult.jsonPropertyType
                assertTrue extensionProcessReadResult.value instanceof BigDecimal
                assertEquals 150, extensionProcessReadResult.value.toInteger()
            }
            if (extensionProcessReadResult.jsonLabel == "landmark") {
                foundCount++
                assertEquals "S", extensionProcessReadResult.jsonPropertyType
                assertTrue extensionProcessReadResult.value instanceof String
                assertEquals "SMALL RED TREE", extensionProcessReadResult.value
            }
            if (extensionProcessReadResult.jsonLabel == "roomCount") {
                foundCount++
                assertEquals "N", extensionProcessReadResult.jsonPropertyType
                assertTrue extensionProcessReadResult.value instanceof BigDecimal
                assertEquals 10, extensionProcessReadResult.value.toInteger()
            }
            if (extensionProcessReadResult.jsonLabel == "constructionDate") {
                foundCount++
                assertEquals "D", extensionProcessReadResult.jsonPropertyType
                assertTrue extensionProcessReadResult.value instanceof Date
                assertEquals "2013-06-24", new SimpleDateFormat("yyyy-MM-dd").format(extensionProcessReadResult.value)
            }
        }
        assertEquals 4, foundCount
    }


    private def newExensionDefinitionGroupsFirstSet() {
        def extensionDefinitions = []

        def extensionDefinition = new ExtensionDefinition(
                extensionCode: "baseline",
                resourceName: "buildings",
                description: "Test data",
                jsonPath: "/",
                jsonLabel: "maxcapacity",
                jsonPropertyType: "N",
                sqlProcessCode: "HEDM_EXTENSIONS",
                sqlReadRuleCode: "BUILDINGS_READ",
                columnName: "SLBBLDG_MAXIMUM_CAPACITY"
        )
        extensionDefinitions.add(extensionDefinition)

        return extensionDefinitionSourceGroupBuilderService.build(extensionDefinitions)
    }


    private def newExensionDefinitionGroupsBothSets() {
        def extensionDefinitions = []

        def extensionDefinition = new ExtensionDefinition(
                extensionCode: "baseline",
                resourceName: "buildings",
                description: "Test data",
                jsonPath: "/",
                jsonLabel: "maxcapacity",
                jsonPropertyType: "N",
                sqlProcessCode: "HEDM_EXTENSIONS",
                sqlReadRuleCode: "BUILDINGS_READ",
                columnName: "SLBBLDG_MAXIMUM_CAPACITY"
        )
        extensionDefinitions.add(extensionDefinition)

        extensionDefinition = new ExtensionDefinition(
                extensionCode: "baseline",
                resourceName: "buildings",
                description: "Test data",
                jsonPath: "/",
                jsonLabel: "landmark",
                jsonPropertyType: "S",
                sqlProcessCode: "HEDM_EXTENSIONS",
                sqlReadRuleCode: "BUILDINGS_READ",
                columnName: "HEDM_BLDG_LANDMARK"
        )
        extensionDefinitions.add(extensionDefinition)

        extensionDefinition = new ExtensionDefinition(
                extensionCode: "baseline",
                resourceName: "buildings",
                description: "Test data",
                jsonPath: "/",
                jsonLabel: "roomCount",
                jsonPropertyType: "N",
                sqlProcessCode: "HEDM_EXTENSIONS",
                sqlReadRuleCode: "BUILDINGS_READ",
                columnName: "HEDM_BLDG_ROOM_COUNT"
        )
        extensionDefinitions.add(extensionDefinition)

        extensionDefinition = new ExtensionDefinition(
                extensionCode: "baseline",
                resourceName: "buildings",
                description: "Test data",
                jsonPath: "/",
                jsonLabel: "constructionDate",
                jsonPropertyType: "D",
                sqlProcessCode: "HEDM_EXTENSIONS",
                sqlReadRuleCode: "BUILDINGS_READ",
                columnName: "HEDM_BLDG_CONSTR_DATE"
        )
        extensionDefinitions.add(extensionDefinition)

        return extensionDefinitionSourceGroupBuilderService.build(extensionDefinitions)
    }
}
