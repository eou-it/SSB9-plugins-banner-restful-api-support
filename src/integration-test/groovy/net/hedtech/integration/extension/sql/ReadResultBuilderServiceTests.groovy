/*******************************************************************************
 Copyright 2017-2020 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

package net.hedtech.integration.extension.sql
import net.hedtech.banner.testing.BaseIntegrationTestCase
import net.hedtech.integration.extension.ExtensionDefinition
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by sdorfmei on 5/26/17.
 */
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import static groovy.test.GroovyAssert.* 
 @ Rollback
@ Integration
 class ReadResultBuilderServiceTests extends BaseIntegrationTestCase {

    def readResultBuilderService

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
    void buildResultsWhenNull(){
        when:
        def result = readResultBuilderService.buildResults(null,null)

        expect:
        assertNotNull result
        assertEquals 0, result.size
    }

    @Test
    void buildOneResults(){

        given:
        def extensionDefinitions = []
        def extensionDefinition = new ExtensionDefinition(
                extensionCode: "code123",
                resourceName: "abc123",
                description: "Test data",
                jsonPath: "/",
                jsonLabel: "abc123",
                jsonPropertyType: "S",
                columnName: "columnName"
        )
        extensionDefinitions.add(extensionDefinition)

        def mockSQLResults = []
        def sqlResult = new MockExtensionSQLResult()
        sqlResult.GUID = "ASFsaf"
        sqlResult.columnName = "1232"

        mockSQLResults.add(sqlResult)


        when:
        def result = readResultBuilderService.buildResults(extensionDefinitions,mockSQLResults)

        expect:
        assertNotNull result
        assertEquals 1, result.size
    }


    @Test
    void buildTwoResults(){

        given:
        def extensionDefinitions = []
        def extensionDefinition = new ExtensionDefinition(
                extensionCode: "code123",
                resourceName: "abc123",
                description: "Test data",
                jsonPath: "/",
                jsonLabel: "abc123",
                jsonPropertyType: "S",
                columnName: "columnName"
        )
        extensionDefinitions.add(extensionDefinition)

        def mockSQLResults = []
        def sqlResult = new MockExtensionSQLResult()
        sqlResult.GUID = "aaaaaa"
        sqlResult.columnName = "1232"

        def sqlResult2 = new MockExtensionSQLResult()
        sqlResult2.GUID = "bbbbb"
        sqlResult2.columnName = "44444"

        mockSQLResults.add(sqlResult)
        mockSQLResults.add(sqlResult2)


        when:
        def result = readResultBuilderService.buildResults(extensionDefinitions,mockSQLResults)

        expect:
        assertNotNull result
        assertEquals 2, result.size
    }



}
