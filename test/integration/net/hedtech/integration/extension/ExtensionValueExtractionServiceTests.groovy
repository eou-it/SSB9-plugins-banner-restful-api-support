/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.text.SimpleDateFormat

/**
 * Created by sdorfmei on 7/13/17.
 */
class ExtensionValueExtractionServiceTests extends BaseIntegrationTestCase {

    def extensionValueExtractionService

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
    void givenNullReturnNull(){

        given:
        def result

        when:
        result = extensionValueExtractionService.extractExtensions(null,null)

        expect:
        assertNotNull result
        assertEquals 0, result.size

    }

    @Test
    void givenOneWithASimpleOneFindOne(){

        given:
        ExtensionDefinition extensionDefinition = newExtensionDefinition("/","foo")
        List<ExtensionDefinition> extensionDefinitionList = []
        extensionDefinitionList.add(extensionDefinition)

        def testRequest = '''{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce", "foo":"myExtendedValue"}'''

        def result

        when:
        result = extensionValueExtractionService.extractExtensions(testRequest,extensionDefinitionList)

        expect:
        assertNotNull result
        assertEquals 1, result.size
        assertFalse result[0].valueWasMissing

    }


    @Test
    void givenOneWithANestedOneFindOne(){

        given:
        ExtensionDefinition extensionDefinition = newExtensionDefinition("/nested/","foo")
        List<ExtensionDefinition> extensionDefinitionList = []
        extensionDefinitionList.add(extensionDefinition)

        def testRequest = '''{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce", "code":"myCode","nested":{"id":"84f8578c-a465-4253-91af-10526760c8a0","foo":"myExtendedValue"}}'''

        def result

        when:
        result = extensionValueExtractionService.extractExtensions(testRequest,extensionDefinitionList)

        expect:
        assertNotNull result
        assertEquals 1, result.size
        assertFalse result[0].valueWasMissing

    }

    @Test
    void givenOneWithANestedOneDontFind(){

        given:
        ExtensionDefinition extensionDefinition = newExtensionDefinition("/nested/","car")
        List<ExtensionDefinition> extensionDefinitionList = []
        extensionDefinitionList.add(extensionDefinition)

        def testRequest = '''{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce", "code":"myCode","nested":{"id":"84f8578c-a465-4253-91af-10526760c8a0","foo":"myExtendedValue"}}'''

        def result

        when:
        result = extensionValueExtractionService.extractExtensions(testRequest,extensionDefinitionList)

        expect:
        assertNotNull result
        assertEquals 1, result.size
        assertTrue result[0].valueWasMissing

    }

    @Test
    void givenOneString(){

        given:
        ExtensionDefinition extensionDefinition = newExtensionDefinition("/","foo")
        List<ExtensionDefinition> extensionDefinitionList = []
        extensionDefinitionList.add(extensionDefinition)

        def testRequest = '''{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce", "foo":"abc"}'''

        def result

        when:
        result = extensionValueExtractionService.extractExtensions(testRequest,extensionDefinitionList)

        expect:
        assertNotNull result
        assertEquals 1, result.size
        assertFalse result[0].valueWasMissing
        assertTrue result[0].value instanceof String
        assertEquals "abc", result[0].value

    }

    @Test
    void givenOneInteger(){

        given:
        ExtensionDefinition extensionDefinition = newExtensionDefinition("/","foo","N")
        List<ExtensionDefinition> extensionDefinitionList = []
        extensionDefinitionList.add(extensionDefinition)

        def testRequest = '''{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce", "foo":123}'''

        def result

        when:
        result = extensionValueExtractionService.extractExtensions(testRequest,extensionDefinitionList)

        expect:
        assertNotNull result
        assertEquals 1, result.size
        assertFalse result[0].valueWasMissing
        assertTrue result[0].value instanceof Number
        assertEquals 123, result[0].value

    }

    @Test
    void givenOneDecimal(){

        given:
        ExtensionDefinition extensionDefinition = newExtensionDefinition("/","foo","N")
        List<ExtensionDefinition> extensionDefinitionList = []
        extensionDefinitionList.add(extensionDefinition)

        def testRequest = '''{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce", "foo":123.456}'''

        def result

        when:
        result = extensionValueExtractionService.extractExtensions(testRequest,extensionDefinitionList)

        expect:
        assertNotNull result
        assertEquals 1, result.size
        assertFalse result[0].valueWasMissing
        assertTrue result[0].value instanceof Number
        assertEquals 123.456, result[0].value, 0.000001

    }

    @Test
    void givenOneDate(){

        given:
        ExtensionDefinition extensionDefinition = newExtensionDefinition("/","foo","D")
        List<ExtensionDefinition> extensionDefinitionList = []
        extensionDefinitionList.add(extensionDefinition)

        def testRequest = '''{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce", "foo":"1982-01-05"}'''

        def result

        when:
        result = extensionValueExtractionService.extractExtensions(testRequest,extensionDefinitionList)

        expect:
        assertNotNull result
        assertEquals 1, result.size
        assertFalse result[0].valueWasMissing
        assertTrue result[0].value instanceof Date
        assertEquals "1982-01-05", new SimpleDateFormat("yyyy-MM-dd").format(result[0].value)

    }

    @Test
    void givenOneTimestamp(){

        given:
        ExtensionDefinition extensionDefinition = newExtensionDefinition("/","foo","T")
        List<ExtensionDefinition> extensionDefinitionList = []
        extensionDefinitionList.add(extensionDefinition)

        def testRequest = '''{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce", "foo":"1982-01-05T05:00:00+00:00"}'''

        def result

        when:
        result = extensionValueExtractionService.extractExtensions(testRequest,extensionDefinitionList)

        expect:
        assertNotNull result
        assertEquals 1, result.size
        assertFalse result[0].valueWasMissing
        assertTrue result[0].value instanceof Date
        assertEquals "1982-01-05T05:00:00+00:00", formatTimestamp(result[0].value)

    }


    private def newExtensionDefinition(def jsonPath, def jsonLabel, def jsonPropertyType = "S") {

        ExtensionDefinitionCode extensionDefinitionCode = new ExtensionDefinitionCode()
        extensionDefinitionCode.code = "code123"
        extensionDefinitionCode.description = "code123"

        def extensionDefinition = new ExtensionDefinition(
                extensionCode: "code123",
                resourceName: "abc123",
                description: "Test data",
                jsonPath: jsonPath,
                jsonLabel: jsonLabel,
                jsonPropertyType: jsonPropertyType,
                columnName: "columnName"
        )
        return extensionDefinition
    }


    private def formatTimestamp(def timestamp) {
        def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        dateFormatter.setTimeZone(TimeZone.getTimeZone('UTC'))
        return dateFormatter.format(timestamp)+"+00:00"
    }
}
