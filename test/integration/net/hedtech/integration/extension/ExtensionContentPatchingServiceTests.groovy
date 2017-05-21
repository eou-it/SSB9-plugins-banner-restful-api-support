package net.hedtech.integration.extension

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
/**
 * Created by sdorfmei on 5/19/17.
 */
class ExtensionContentPatchingServiceTests extends BaseIntegrationTestCase {

    def extensionContentPatchingService

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
    public void testOneResource() {
        def oneResource = '''{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        ExtensionProcessReadResult extensionProcessReadResult = new ExtensionProcessReadResult()
        extensionProcessReadResult.jsonPath = "/"
        extensionProcessReadResult.jsonLabel="newField"
        extensionProcessReadResult.jsonType = "property"
        extensionProcessReadResult.value = "500"
        extensionProcessReadResult.resourceId = "24c47f0a-0eb7-48a3-85a6-2c585691c6ce"

        def extensionProcessReadResults = []
        extensionProcessReadResults.add(extensionProcessReadResult)

        def result = extensionContentPatchingService.patchExtensions(extensionProcessReadResults,oneResource)

    }
}
