/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

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


    private def newExtensionDefinition(def jsonPath, def jsonLabel) {

        ExtensionDefinitionCode extensionDefinitionCode = new ExtensionDefinitionCode()
        extensionDefinitionCode.code = "code123"
        extensionDefinitionCode.description = "code123"

        def extensionDefinition = new ExtensionDefinition(
                extensionType: "baseline",
                extensionCode: "code123",
                resourceName: "abc123",
                description: "Test data",
                jsonPath: jsonPath,
                jsonType: "property",
                jsonLabel: jsonLabel,
                columnName: "columnName",
                version: 0,
                lastModified: new Date(),
                lastModifiedBy: "test",
                dataOrigin: "Banner"
        )
        return extensionDefinition
    }
}
