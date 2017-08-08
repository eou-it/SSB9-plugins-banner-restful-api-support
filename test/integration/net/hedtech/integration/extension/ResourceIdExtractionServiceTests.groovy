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

class ResourceIdExtractionServiceTests extends BaseIntegrationTestCase {

    def resourceIdExtractionService


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
    void extractId_whenNullExpectNull() {
        def resourceId = resourceIdExtractionService.extractIdFromNode(null)
        assertNull resourceId
    }

    @Test
    void extractList_whenNullExpectNull() {
        def resourceIdList = resourceIdExtractionService.extractIdListFromNode(null)
        assertNull resourceIdList
    }

    @Test
    void extractId_testOneResourceContent() {
        def oneResource = '''{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce"}'''

        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(oneResource);
        def resourceId = resourceIdExtractionService.extractIdFromNode(rootNode)
        assertNotNull resourceId
        assertTrue resourceId == '24c47f0a-0eb7-48a3-85a6-2c585691c6ce'
    }

    @Test
    void extractList_testManyResourceContent() {
        def arrayOfResources = '''
                [{"id": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce"},
                 {"id": "26a2673f-9bc6-4649-a3e8-213d0ff4afbd"},
                 {"id": "26a2243f-9af6-4649-a3e8-213222f4afsf"}
                ]'''

        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(arrayOfResources);
        def resultList = resourceIdExtractionService.extractIdListFromNode(rootNode)
        assertNotNull resultList
        assertTrue resultList.size == 3
    }

    @Test
    void extractList_testManyResourceContentNoIds() {
        def arrayOfResources = '''
                [{"fff": "24c47f0a-0eb7-48a3-85a6-2c585691c6ce"},
                 {"bbb": "26a2673f-9bc6-4649-a3e8-213d0ff4afbd"},
                 {"ccc": "26a2243f-9af6-4649-a3e8-213222f4afsf"}
                ]'''

        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(arrayOfResources);
        def resultList = resourceIdExtractionService.extractIdListFromNode(rootNode)
        assertNotNull resultList
        assertTrue resultList.size == 0
    }
}
