package net.hedtech.integration.extension.sql
import net.hedtech.banner.testing.BaseIntegrationTestCase
import net.hedtech.integration.extension.ExtensionDefinition
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by sdorfmei on 5/26/17.
 */
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
        result == null
    }

    @Test
    void buildOneResults(){

        given:
        def extensionDefinitions = []
        def extensionDefinition = new ExtensionDefinition(
                extensionType: "baseline",
                extensionCode: "code123",
                resourceName: "abc123",
                description: "Test data",
                jsonPath: "/",
                jsonType: "property",
                jsonlabel: "abc123",
                selectColumnName: "selectColumn",
                version: 0,
                jsonLabel: "abc123",
                lastModified: new Date(),
                lastModifiedBy: "test",
                dataOrigin: "Banner"
        )
        extensionDefinitions.add(extensionDefinition)

        def mockSQLResults = []
        def sqlResult = new MockExtensionSQLResult()
        sqlResult.GUID = "ASFsaf"
        sqlResult.selectColumn = "1232"

        mockSQLResults.add(sqlResult)


        when:
        def result = readResultBuilderService.buildResults(extensionDefinitions,mockSQLResults)

        expect:
        result != null
        result.size == 1
    }


    @Test
    void buildTwoResults(){

        given:
        def extensionDefinitions = []
        def extensionDefinition = new ExtensionDefinition(
                extensionType: "baseline",
                extensionCode: "code123",
                resourceName: "abc123",
                description: "Test data",
                jsonPath: "/",
                jsonType: "property",
                jsonlabel: "abc123",
                selectColumnName: "selectColumn",
                version: 0,
                jsonLabel: "abc123",
                lastModified: new Date(),
                lastModifiedBy: "test",
                dataOrigin: "Banner"
        )
        extensionDefinitions.add(extensionDefinition)

        def mockSQLResults = []
        def sqlResult = new MockExtensionSQLResult()
        sqlResult.GUID = "aaaaaa"
        sqlResult.selectColumn = "1232"

        def sqlResult2 = new MockExtensionSQLResult()
        sqlResult2.GUID = "bbbbb"
        sqlResult2.selectColumn = "44444"

        mockSQLResults.add(sqlResult)
        mockSQLResults.add(sqlResult2)


        when:
        def result = readResultBuilderService.buildResults(extensionDefinitions,mockSQLResults)

        expect:
        result != null
        result.size == 1
    }



}
