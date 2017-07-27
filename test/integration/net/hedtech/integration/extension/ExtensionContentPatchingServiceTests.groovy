/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.text.SimpleDateFormat

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
        assertNull result

    }

    @Test
    void buildPatch_givenNoPath(){

        given:
        String result
        ExtensionProcessReadResult extensionProcessReadResult = new ExtensionProcessReadResult()
        extensionProcessReadResult.jsonLabel = "foo"
        extensionProcessReadResult.value = "123"

        when:
        result = extensionContentPatchingService.buildPatch(extensionProcessReadResult)

        expect:
        assertNull result

    }

    @Test
    void buildPatch_givenExpected(){

        given:
        String result
        ExtensionProcessReadResult extensionProcessReadResult = new ExtensionProcessReadResult()
        extensionProcessReadResult.jsonLabel = "foo"
        extensionProcessReadResult.value = "123"
        extensionProcessReadResult.jsonPath = "/path"

        when:
        result = extensionContentPatchingService.buildPatch(extensionProcessReadResult)

        expect:
        assertNotNull result
        assertEquals '[{"op":"add","path":"/path/foo","value":"123"}]', result

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
        assertNotNull resourceList
        assertEquals 1, resourceList.size

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
        assertNotNull resourceList
        assertEquals 0, resourceList.size
    }

    @Test
    void testOneResource() {

        given:
        def oneResource = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newField",
                "SS","500","24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        String expectedResult = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce","newField":"500"}'''
        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def result = extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)

        expect:
        assertNotNull result
        assertEquals expectedResult, result

    }

    @Test
    void testOneResourceTwoProperties() {

        given:
        def oneResource = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newField",
                "S","500","24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newField2",
                "S","99999","24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        String expectedResult = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce","newField":"500","newField2":"99999"}'''
        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def result = extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)

        expect:
        assertNotNull result
        assertEquals expectedResult, result

    }

    @Test
    void testTwoResource() {

        given:
        def twoResources = '''[{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce","foo":"bar","cat":"dog"},{"id":"26a2673f-9bc6-4649-a3e8-213d0ff4afbd","foo":"bar","cat":"dog"}]'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newField","S","500","24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newField","S","600","26a2673f-9bc6-4649-a3e8-213d0ff4afbd"))

        String expectedResult = '''[{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce","foo":"bar","cat":"dog","newField":"500"},'''+
                '''{"id":"26a2673f-9bc6-4649-a3e8-213d0ff4afbd","foo":"bar","cat":"dog","newField":"600"}]'''

        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(twoResources);

        when:
        def result = extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)

        expect:
        assertNotNull result
        assertEquals expectedResult, result


    }

    @Test
    void testArray() {

        given:
        def oneResource = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newField",
                "S","500","24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        String expectedResult = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce","newField":"500"}'''
        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def result = extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)

        expect:
        assertNotNull result
        assertEquals expectedResult, result

    }

    @Test
    void testNumbers() {

        given:
        def oneResource = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newInteger",
                "N",123,"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newDecimal",
                "N",123.456,"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newNegativeNumber",
                "N",-123.456,"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newPositiveNumber",
                "N",+7890,"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        String expectedResult = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce","newInteger":123,'''+
                '''"newDecimal":123.456,"newNegativeNumber":-123.456,"newPositiveNumber":7890}'''
        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def result = extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)

        expect:
        assertNotNull result
        assertEquals expectedResult, result

    }

    @Test
    void testDates() {

        given:
        def oneResource = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        def currentDate = new Date()
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newDate",
                "D",currentDate,"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newTimestamp",
                "T",currentDate,"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))


        String expectedDate = new SimpleDateFormat("yyyy-MM-dd").format(currentDate)
        def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        dateFormatter.setTimeZone(TimeZone.getTimeZone('UTC'))
        String expectedTimestamp = dateFormatter.format(currentDate)+"+00:00"
        String expectedResult = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce",'''+
                '''"newDate":"''' + expectedDate + '''",'''+
                '''"newTimestamp":"''' + expectedTimestamp + '''"}'''
        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def result = extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)

        expect:
        assertNotNull result
        assertEquals expectedResult, result

    }

    private ExtensionProcessReadResult newExtensionProcessReadResult(String p_jsonPath,
                                                                     String p_jsonLabel,
                                                                     String p_jsonPropertyType,
                                                                     def p_value,
                                                                     String p_resourceId){
        ExtensionProcessReadResult extensionProcessReadResult = new ExtensionProcessReadResult()
        extensionProcessReadResult.jsonPath = p_jsonPath
        extensionProcessReadResult.jsonLabel= p_jsonLabel
        extensionProcessReadResult.jsonPropertyType = p_jsonPropertyType
        extensionProcessReadResult.value = p_value
        extensionProcessReadResult.resourceId = p_resourceId

        return extensionProcessReadResult

    }
}
