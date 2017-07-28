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

/**
 * Created by sdorfmei on 5/16/17.
 */
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
    void givenOne() {

        //Get a GUID by looking at GORGUID and grabbing one (support for every developers GUIDs)
        def buildingGuidList = []
        def guidQuery = "SELECT * FROM (select gorguid_guid from gorguid where gorguid_ldm_name = 'buildings') gorguid WHERE rownum <= 1 ORDER BY rownum"
        def sqlQuery = sessionFactory.currentSession.createSQLQuery(guidQuery)
        def guidResults = sqlQuery.list()
        guidResults.each { row ->
            buildingGuidList.add(row)
        }

        def resultList = readCompositeService.read(newExensionDefinitionGroups(),buildingGuidList)

        assertNotNull resultList
    }



    private def newExensionDefinitionGroups() {
        def extensionDefinitions = []

        def extensionDefinition = new ExtensionDefinition(
                extensionType: "baseline",
                resourceName: "buildings",
                description: "Test data",
                jsonPath: "/",
                jsonlabel: "maxcapacity",
                jsonPropertyType: "N",
                sqlProcessCode: "HEDM_EXTENSIONS",
                sqlReadRuleCode: "BUILDINGS_READ",
                columnName: "SLBBLDG_MAXIMUM_CAPACITY"
        )

        extensionDefinitions.add(extensionDefinition)
        return extensionDefinitionSourceGroupBuilderService.build(extensionDefinitions)
    }
}
