/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.After
import org.junit.Before
import org.junit.Test

import net.hedtech.banner.testing.BaseIntegrationTestCase
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
    void buildPatch_givennull(){

        given:
        String result

        when:
        result = extensionContentPatchingService.buildPatch(null)

        expect:
        result == null

    }
    @Test
    void buildPatch_givenNoPath(){

        given:
        String result
        ExtensionProcessReadResult extensionProcessReadResult = new ExtensionProcessReadResult()
        extensionProcessReadResult.jsonLabel = "foo"
        extensionProcessReadResult.value = "!23"

        when:
        result = extensionContentPatchingService.buildPatch(extensionProcessReadResult)

        expect:
        result == null

    }

    @Test
    void buildPatch_givenExpected(){

        given:
        String result
        ExtensionProcessReadResult extensionProcessReadResult = new ExtensionProcessReadResult()
        extensionProcessReadResult.jsonLabel = "foo"
        extensionProcessReadResult.value = "!23"
        extensionProcessReadResult.jsonPath = "/path"

        when:
        result = extensionContentPatchingService.buildPatch(extensionProcessReadResult)

        expect:
        result != null
        result == '[{"op":"add","path":"/path/foo","value":"!123"}]';

    }

    @Test
    void extensionFilterResults_givenExpected(){
        given:
        ExtensionProcessReadResult extensionProcessReadResult = new ExtensionProcessReadResult()
        extensionProcessReadResult.resourceId = "123"
        ExtensionProcessReadResult extensionProcessReadResult2 = new ExtensionProcessReadResult()
        extensionProcessReadResult2.resourceId = "456"
        ExtensionProcessReadResult extensionProcessReadResult3 = new ExtensionProcessReadResult()
        extensionProcessReadResult3.resourceId = "789"

        def extensions = []
        extensions.add(extensionProcessReadResult)
        extensions.add(extensionProcessReadResult2)
        extensions.add(extensionProcessReadResult3)

        when:
        def resourceList = extensionContentPatchingService.getExtensionsForResourceId("456", extensions)

        expect:
        resourceList != null
        resourceList.size == 1

    }

    @Test
    void extensionFilterResults_givenMissing(){
        given:
        ExtensionProcessReadResult extensionProcessReadResult = new ExtensionProcessReadResult()
        extensionProcessReadResult.resourceId = "123"
        ExtensionProcessReadResult extensionProcessReadResult2 = new ExtensionProcessReadResult()
        extensionProcessReadResult2.resourceId = "456"
        ExtensionProcessReadResult extensionProcessReadResult3 = new ExtensionProcessReadResult()
        extensionProcessReadResult3.resourceId = "789"

        def extensions = []
        extensions.add(extensionProcessReadResult)
        extensions.add(extensionProcessReadResult2)
        extensions.add(extensionProcessReadResult3)

        when:
        def resourceList = extensionContentPatchingService.getExtensionsForResourceId("ABC", extensions)

        expect:
        resourceList != null
        resourceList.size == 0
    }
    @Test
    void testOneResource() {

        given:
        def oneResource = '''{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newField",
                "property","500","24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        String expectedResult = '''{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce","newField":"500"}'''
        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def result = extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)

        expect:
        result != null
        result == expectedResult

    }

    @Test
    void testOneResourceTwoProperties() {

        given:
        def oneResource = '''{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newField",
                "property","500","24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newField2",
                "property","99999","24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        String expectedResult = '''{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce","newField":"500","newField2":"99999"}'''
        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def result = extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)

        expect:
        result != null
        result == expectedResult

    }
    @Test
    void testTwoResource() {

        given:
        def twoResources = '''[{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce","foo":"bar","cat":"dog"},{"id": "26a2673f-9bc6-4649-a3e8-213d0ff4afbd","foo":"bar","cat":"dog"}]'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newField","property","500","24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newField","property","600","26a2673f-9bc6-4649-a3e8-213d0ff4afbd"))

        String expectedResult = '''{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce","foo":"bar","cat":"dog","newField":"500},
                                    "id": "26a2673f-9bc6-4649-a3e8-213d0ff4afbd","foo":"bar","cat":"dog","newField":"600}'''

        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(twoResources);

        when:
        def result = extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)

        expect:
        result != null
        result == expectedResult


    }

    @Test
    void testArray() {

        given:
        def oneResource = '''{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newField",
                "property","500","24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        String expectedResult = '''{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce","newField":"500"}'''
        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def result = extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)

        expect:
        result != null
        result == expectedResult

    }

    private ExtensionProcessReadResult newExtensionProcessReadResult(String p_jsonPath,
                                                                     String p_jsonLabel,
                                                                     String p_jsonType,
                                                                     String p_value,
                                                                     String p_resourceId){
        ExtensionProcessReadResult extensionProcessReadResult = new ExtensionProcessReadResult()
        extensionProcessReadResult.jsonPath = p_jsonPath
        extensionProcessReadResult.jsonLabel= p_jsonLabel
        extensionProcessReadResult.jsonType = p_jsonType
        extensionProcessReadResult.value = p_value
        extensionProcessReadResult.resourceId = p_resourceId

        return extensionProcessReadResult

    }
}
