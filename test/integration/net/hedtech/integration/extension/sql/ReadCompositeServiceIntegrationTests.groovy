package net.hedtech.integration.extension.sql
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

    @Test
    void test() {

        def extensionDefinition1 = newExensionDefinition()
        save extensionDefinition1

        def resultList = readCompositeService.read([extensionDefinition1],null)

        assertNotNull resultList
    }



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
