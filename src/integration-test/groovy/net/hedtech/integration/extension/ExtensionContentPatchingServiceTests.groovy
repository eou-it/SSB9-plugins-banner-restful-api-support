/*******************************************************************************
 Copyright 2017-2020 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

package net.hedtech.integration.extension

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.text.SimpleDateFormat

import net.hedtech.banner.testing.BaseIntegrationTestCase

import net.hedtech.integration.extension.exceptions.JsonExtensibilityParseException
import net.hedtech.integration.extension.exceptions.JsonExtensibilityPropertyPatchException
import net.hedtech.integration.extension.exceptions.JsonExtensibilityArrayPatchException
import net.hedtech.integration.extension.exceptions.JsonPropertyTypeMismatchException

/**
 * Created by sdorfmei on 5/19/17.
 */
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import static groovy.test.GroovyAssert.* 
 @ Rollback
@ Integration
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
        extensionProcessReadResult.jsonPath = "/path"
        extensionProcessReadResult.jsonLabel = "foo"
        extensionProcessReadResult.jsonPropertyType = "S"
        extensionProcessReadResult.value = "123"

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

    @Test
    void testJsonText() {

        given:
        def oneResource = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newJsonText",
                "J",'{"field1":"abc","field2":123,"nested":{"nested-field":"xyz"}}',"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        String expectedResult = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce",'''+
                '''"newJsonText":{"field1":"abc","field2":123,"nested":{"nested-field":"xyz"}}}'''
        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def result = extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)

        expect:
        assertNotNull result
        assertEquals expectedResult, result

    }

    @Test
    void testArrayOfJsonStrings() {

        given:
        def oneResource = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","newJsonText",
                "J",'["guid1","guid2","guid3"]',"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        String expectedResult = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce",'''+
                '''"newJsonText":["guid1","guid2","guid3"]}'''
        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def result = extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)

        expect:
        assertNotNull result
        assertEquals expectedResult, result

    }

    @Test
    void testInvalidString() {

        given:
        def oneResource = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","foo","S",123,"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def errorMessage = shouldFail(JsonPropertyTypeMismatchException) {
            extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)
        }

        expect:
        assertEquals "Property /foo must be a valid String", errorMessage?.getMessage()

    }

    @Test
    void testInvalidNumber() {

        given:
        def oneResource = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","foo","N","abc","24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def errorMessage = shouldFail(JsonPropertyTypeMismatchException) {
            extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)
        }

        expect:
        assertEquals "Property /foo must be a valid Number", errorMessage?.getMessage()

    }

    @Test
    void testInvalidDate() {

        given:
        def oneResource = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","foo","D","01-05-1982","24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def errorMessage = shouldFail(JsonPropertyTypeMismatchException) {
            extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)
        }

        expect:
        assertEquals "Property /foo must be a valid Date using format yyyy-MM-dd", errorMessage?.getMessage()

    }

    @Test
    void testInvalidTimestamp() {

        given:
        def oneResource = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","foo","T","1982-01-05T05:00:00","24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def errorMessage = shouldFail(JsonPropertyTypeMismatchException) {
            extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)
        }

        expect:
        assertEquals "Property /foo must be a valid Date using format yyyy-MM-dd'T'HH:mm:ssX", errorMessage?.getMessage()

    }

    @Test
    void testInvalidJsonText() {

        given:
        def oneResource = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","foo",
                "J",123,"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def errorMessage = shouldFail(JsonPropertyTypeMismatchException) {
            extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)
        }

        expect:
        assertEquals "Property /foo must be valid JSON text", errorMessage?.getMessage()

    }

    @Test
    void testUnparseableJsonText() {

        given:
        def oneResource = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","foo",
                "J",'abc',"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def errorMessage = shouldFail(JsonExtensibilityParseException) {
            extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)
        }

        expect:
        assertEquals "Unparseable JSON text for property /foo with resource id=24c47f0a-0eb7-48a3-85a6-2c585691c6ce; error=Unrecognized token 'abc': was expecting ('true', 'false' or 'null')", errorMessage?.getMessage().substring(0, 168)

    }

    @Test
    void testUnparseableJsonTextMissingLeadingCurlyBrace() {

        given:
        def oneResource = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","foo",
                "J",'"field1":"abc","field2":123,"nested":{"nested-field":"xyz"}}',"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def errorMessage = shouldFail(JsonExtensibilityParseException) {
            extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)
        }

        expect:
        assertEquals "Unparseable JSON text for property /foo with resource id=24c47f0a-0eb7-48a3-85a6-2c585691c6ce; error=Unexpected character (':' (code 58)): was expecting comma to separate Object entries", errorMessage?.getMessage().substring(0, 185)

    }

    @Test
    void testJsonTextInStringField() {

        given:
        def oneResource = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","foo",
                "S",'{"abc":"def"}',"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def errorMessage = shouldFail(JsonExtensibilityPropertyPatchException) {
            extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)
        }

        expect:
        assertEquals "Unable to apply JSON patch for property /foo using property type of S with resource id=24c47f0a-0eb7-48a3-85a6-2c585691c6ce; error=Unexpected character ('a' (code 97)): was expecting comma to separate Object entries", errorMessage?.getMessage().substring(0, 215)

    }

    @Test
    void testInvalidJsonPropertyType() {

        given:
        def oneResource = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/","foo","X","abc","24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def errorMessage = shouldFail(JsonPropertyTypeMismatchException) {
            extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)
        }

        expect:
        assertEquals "Property type X is invalid for property /foo", errorMessage?.getMessage()

    }

    @Test
    void findNestedPaths_givennull(){

        given:
        Map result

        when:
        result = extensionContentPatchingService.findNestedPaths(null)

        expect:
        assertNotNull result
        assertEquals 0, result.size()

    }

    @Test
    void findNestedPaths_givenNoPath(){

        given:
        Map result
        ExtensionProcessReadResult extensionProcessReadResult = new ExtensionProcessReadResult()
        extensionProcessReadResult.jsonLabel = "foo"
        extensionProcessReadResult.value = "123"
        List extensionResultList = [extensionProcessReadResult]

        when:
        result = extensionContentPatchingService.findNestedPaths(extensionResultList)

        expect:
        assertNotNull result
        assertEquals 0, result.size()

    }

    @Test
    void findNestedPaths_givenExpected(){

        given:
        Map result
        ExtensionProcessReadResult extensionProcessReadResult = new ExtensionProcessReadResult()
        extensionProcessReadResult.jsonPath = "/path"
        extensionProcessReadResult.jsonLabel = "foo"
        extensionProcessReadResult.jsonPropertyType = "S"
        extensionProcessReadResult.value = "123"
        List extensionResultList = [extensionProcessReadResult]

        when:
        result = extensionContentPatchingService.findNestedPaths(extensionResultList)

        expect:
        assertNotNull result
        assertEquals "[path:[:]]", result.toString()
        assertEquals 1, result.size()
        def value = result.get("path")
        assertNotNull value
        assertTrue (value instanceof Map)
        assertEquals 0, value.size()

    }

    @Test
    void findNestedPaths_complex(){

        given:
        Map result
        def extensionResultList = newExtensionResultList()

        when:
        result = extensionContentPatchingService.findNestedPaths(extensionResultList)

        expect:
        assertNotNull result
        assertEquals 2, result.size()
        assertEquals "[extensions:[:], moreExtensions:[nested:[levela:[:]], levelb:[:], levelc:[:]]]", result.toString()
        def value1 = result.get("extensions")
        assertNotNull value1
        assertTrue (value1 instanceof Map)
        assertEquals 0, value1.size()
        def value2 = result.get("moreExtensions")
        assertNotNull value2
        assertTrue (value2 instanceof Map)
        assertEquals 3, value2.size()
        def value2a = value2.get("nested")
        assertNotNull value2a
        assertTrue (value2a instanceof Map)
        assertEquals 1, value2a.size()
        def value2a1 = value2a.get("levela")
        assertNotNull value2a1
        assertTrue (value2a1 instanceof Map)
        assertEquals 0, value2a1.size()
        def value2b = value2.get("levelb")
        assertNotNull value2b
        assertTrue (value2b instanceof Map)
        assertEquals 0, value2b.size()
        def value2c = value2.get("levelc")
        assertNotNull value2c
        assertTrue (value2c instanceof Map)
        assertEquals 0, value2c.size()

    }

    @Test
    void buildNestedPathsPatch_simple(){

        given:
        String result
        String label = "extensions"
        Map nestedPaths = [extensions:[:], moreExtensions:[nested:[levela:[:]], levelb:[:], levelc:[:]]]

        when:
        result = extensionContentPatchingService.buildNestedPathsPatch(label, nestedPaths.get(label))

        expect:
        assertNotNull result
        assertEquals '[{"op":"add","path":"extensions","value":{}}]', result

    }

    @Test
    void buildNestedPathsPatch_complex(){

        given:
        String result
        String label = "moreExtensions"
        Map nestedPaths = [extensions:[:], moreExtensions:[nested:[levela:[:]], levelb:[:], levelc:[:]]]

        when:
        result = extensionContentPatchingService.buildNestedPathsPatch(label, nestedPaths.get(label))

        expect:
        assertNotNull result
        assertEquals '[{"op":"add","path":"moreExtensions","value":{"nested":{"levela":{}},"levelb":{},"levelc":{}}}]', result

    }

    @Test
    void testOneResource_nestedPaths(){

        given:
        def oneResource = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''
        def extensionResultList = newExtensionResultList()

        String expectedResult = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce",'''+
                '''"extensions":{"field2a":"123","field2b":"123"},'''+
                '''"moreExtensions":{"nested":{"levela":{"field3a":"123","field3b":"123"}},'''+
                    '''"levelb":{"field4a":"123"},'''+
                    '''"levelc":{"field4b":"123"}},'''+
                '''"field1a":"123","field1b":"123"}'''
        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def result = extensionContentPatchingService.patchExtensions(extensionResultList, rootNode)

        expect:
        assertNotNull result
        assertEquals expectedResult, result

    }

    @Test
    void testArrayPath_nestedPaths() {

        given:
        def oneResource = '''{"id":"24c47f0a-0eb7-48a3-85a6-2c585691c6ce","list":[{"name1":"value1"},{"name2":"value2"}]}'''
        def extensionProcessReadResults = []
        extensionProcessReadResults.add(newExtensionProcessReadResult("/list","foo",
                "S","abc","24c47f0a-0eb7-48a3-85a6-2c585691c6ce"))

        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);

        when:
        def errorMessage = shouldFail(JsonExtensibilityArrayPatchException) {
            extensionContentPatchingService.patchExtensions(extensionProcessReadResults,rootNode)
        }

        expect:
        assertEquals "Unable to apply JSON patch for array path /list/foo with resource id=24c47f0a-0eb7-48a3-85a6-2c585691c6ce; error=For input string: \"foo\"", errorMessage?.getMessage()

    }

    private ExtensionProcessReadResult newExtensionProcessReadResult(String jsonPath,
                                                                     String jsonLabel,
                                                                     String jsonPropertyType,
                                                                     def value,
                                                                     String resourceId){
        ExtensionProcessReadResult extensionProcessReadResult = new ExtensionProcessReadResult()
        extensionProcessReadResult.jsonPath = jsonPath
        extensionProcessReadResult.jsonLabel= jsonLabel
        extensionProcessReadResult.jsonPropertyType = jsonPropertyType
        extensionProcessReadResult.value = value
        extensionProcessReadResult.resourceId = resourceId

        return extensionProcessReadResult

    }

    private List<ExtensionProcessReadResult> newExtensionResultList(){
        List<ExtensionProcessReadResult> extensionResultList = []

        ExtensionProcessReadResult extensionProcessReadResult1a = new ExtensionProcessReadResult()
        extensionProcessReadResult1a.jsonPath = "/"
        extensionProcessReadResult1a.jsonLabel = "field1a"
        extensionProcessReadResult1a.jsonPropertyType = "S"
        extensionProcessReadResult1a.value = "123"
        extensionResultList.add(extensionProcessReadResult1a)

        ExtensionProcessReadResult extensionProcessReadResult1b = new ExtensionProcessReadResult()
        extensionProcessReadResult1b.jsonPath = "/"
        extensionProcessReadResult1b.jsonLabel = "field1b"
        extensionProcessReadResult1b.jsonPropertyType = "S"
        extensionProcessReadResult1b.value = "123"
        extensionResultList.add(extensionProcessReadResult1b)

        ExtensionProcessReadResult extensionProcessReadResult2a = new ExtensionProcessReadResult()
        extensionProcessReadResult2a.jsonPath = "/extensions"
        extensionProcessReadResult2a.jsonLabel = "field2a"
        extensionProcessReadResult2a.jsonPropertyType = "S"
        extensionProcessReadResult2a.value = "123"
        extensionResultList.add(extensionProcessReadResult2a)

        ExtensionProcessReadResult extensionProcessReadResult2b = new ExtensionProcessReadResult()
        extensionProcessReadResult2b.jsonPath = "/extensions"
        extensionProcessReadResult2b.jsonLabel = "field2b"
        extensionProcessReadResult2b.jsonPropertyType = "S"
        extensionProcessReadResult2b.value = "123"
        extensionResultList.add(extensionProcessReadResult2b)

        ExtensionProcessReadResult extensionProcessReadResult3a = new ExtensionProcessReadResult()
        extensionProcessReadResult3a.jsonPath = "/moreExtensions/nested/levela"
        extensionProcessReadResult3a.jsonLabel = "field3a"
        extensionProcessReadResult3a.jsonPropertyType = "S"
        extensionProcessReadResult3a.value = "123"
        extensionResultList.add(extensionProcessReadResult3a)

        ExtensionProcessReadResult extensionProcessReadResult3b = new ExtensionProcessReadResult()
        extensionProcessReadResult3b.jsonPath = "/moreExtensions/nested/levela"
        extensionProcessReadResult3b.jsonLabel = "field3b"
        extensionProcessReadResult3b.jsonPropertyType = "S"
        extensionProcessReadResult3b.value = "123"
        extensionResultList.add(extensionProcessReadResult3b)

        ExtensionProcessReadResult extensionProcessReadResult4a = new ExtensionProcessReadResult()
        extensionProcessReadResult4a.jsonPath = "/moreExtensions/levelb"
        extensionProcessReadResult4a.jsonLabel = "field4a"
        extensionProcessReadResult4a.jsonPropertyType = "S"
        extensionProcessReadResult4a.value = "123"
        extensionResultList.add(extensionProcessReadResult4a)

        ExtensionProcessReadResult extensionProcessReadResult4b = new ExtensionProcessReadResult()
        extensionProcessReadResult4b.jsonPath = "/moreExtensions/levelc"
        extensionProcessReadResult4b.jsonLabel = "field4b"
        extensionProcessReadResult4b.jsonPropertyType = "S"
        extensionProcessReadResult4b.value = "123"
        extensionResultList.add(extensionProcessReadResult4b)

        return extensionResultList
    }
}
