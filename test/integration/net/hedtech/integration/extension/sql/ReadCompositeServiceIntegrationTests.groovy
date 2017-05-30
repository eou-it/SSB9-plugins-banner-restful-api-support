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
                resourceCatalog: "abc123",
                description: "Test data",
                jsonPath: "/",
                jsonType: "property",
                jsonlabel: "maxcapacity",
                selectColumnName: "SLBBLDG_MAXIMUM_CAPACITY",
                sqlRuleCode: "BUILDINGS",
                sqlProcessCode: "HEDM_EXTENSIONS",
                version: 0,
                jsonLabel: "abc123",
                lastModified: new Date(),
                lastModifiedBy: "test",
                dataOrigin: "Banner"
        )

        extensionDefinitions.add(extensionDefinition)
        return extensionDefinitionSourceGroupBuilderService.build(extensionDefinitions)
    }
}
