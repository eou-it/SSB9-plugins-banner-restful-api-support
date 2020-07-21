/*******************************************************************************
 Copyright 2017-2020 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension

import net.hedtech.banner.testing.BaseIntegrationTestCase
import net.hedtech.restfulapi.apiversioning.BasicApiVersionParser
import net.hedtech.restfulapi.RepresentationRequestAttributes
import net.hedtech.restfulapi.config.RepresentationConfig
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.text.SimpleDateFormat

import org.springframework.mock.web.MockHttpServletRequest

/**
 * Created by sdorfmei on 5/18/17.
 */
import groovy.util.logging.Slf4j
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import static groovy.test.GroovyAssert.* 
 @ Rollback
@ Slf4j
@ Integration
 class ExtensionProcessCompositeServiceIntegrationTests  extends BaseIntegrationTestCase  {

    def extensionProcessCompositeService

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
    void testNoExtensions() {
        def resourceName = "foo"

        def requestContent = null
        def responseContent = """{"id":"38d4154e-276d-4907-969b-62579cf1b7a6","name":"my test","desc":"my description"}"""

        def request = newMockHttpServletRequest("GET", "application/json", requestContent)

        ExtensionProcessResult extensionProcessResult = extensionProcessCompositeService.applyExtensions(resourceName, request, null, responseContent, false)
        assertNull extensionProcessResult
    }


    @Test
    void testBuildings() {
        def resourceName = "buildings"
        def mediaType = "application/vnd.hedtech.integration.v6+json"

        def guidList = []
        def guidQuery = "select gorguid_guid from gorguid where gorguid_ldm_name = 'buildings' and gorguid_domain_key = 'TECH'"
        def sqlQuery = sessionFactory.currentSession.createSQLQuery(guidQuery)
        def guidResults = sqlQuery.list()
        assertEquals 1, guidResults.size()
        guidResults.each { row ->
            guidList.add(row)
        }

        //////  Test GET method  //////

        def method = "GET"
        def requestContent = null
        def responseContent = """{"id":""" + "\"${guidList[0]}\"" + ""","code":"TECH","title":"Technology Hall"}"""
        def expectedContent = """{"id":""" + "\"${guidList[0]}\"" + ""","code":"TECH","title":"Technology Hall","hedmCapacity":150,"hedmConstructionDate":"2013-06-24","hedmLandmark":"SMALL RED TREE","hedmRoomCount":10}"""

        def request = newMockHttpServletRequest(method, mediaType, requestContent)

        ExtensionProcessResult extensionProcessResult = extensionProcessCompositeService.applyExtensions(resourceName, request, null, responseContent, false)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content
        assertTrue extensionProcessResult.extensionsApplied

        def originalContent = expectedContent

        //////  Test PUT method  //////

        method = "PUT"
        requestContent = """{"id":""" + "\"${guidList[0]}\"" + ""","code":"TECH","title":"Technology Hall","hedmCapacity":750,"hedmConstructionDate":"2012-03-25","hedmLandmark":"BIG RED TREE","hedmRoomCount":500}"""
        responseContent = requestContent
        expectedContent = requestContent

        def updatedContent = expectedContent

        request = newMockHttpServletRequest(method, mediaType, requestContent)

        extensionProcessResult = extensionProcessCompositeService.applyExtensions(resourceName, request, null, responseContent, false)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content
        assertTrue extensionProcessResult.extensionsApplied

        def verifyQuery = '''
                select slbbldg_maximum_capacity,
                       sys.anydata.accessVarchar2(g1.gorsdav_value) as HEDM_BLDG_LANDMARK,
                       sys.anydata.accessNumber(g2.gorsdav_value) as HEDM_BLDG_ROOM_COUNT,
                       sys.anydata.accessDate(g3.gorsdav_value) as HEDM_BLDG_CONSTR_DATE
                  from gorguid g, slbbldg s, gorsdav g1, gorsdav g2, gorsdav g3
                 where gorguid_ldm_name = 'buildings'
                   and g.gorguid_domain_surrogate_id = s.slbbldg_surrogate_id
                   and g1.gorsdav_table_name(+) = 'SLBBLDG'
                   and g2.gorsdav_table_name(+) = 'SLBBLDG'
                   and g3.gorsdav_table_name(+) = 'SLBBLDG'
                   and g1.gorsdav_attr_name(+) = 'HEDM_BLDG_LANDMARK'
                   and g2.gorsdav_attr_name(+) = 'HEDM_BLDG_ROOM_COUNT'
                   and g3.gorsdav_attr_name(+) = 'HEDM_BLDG_CONSTR_DATE'
                   and g1.gorsdav_pk_parenttab(+) = s.slbbldg_bldg_code
                   and g2.gorsdav_pk_parenttab(+) = s.slbbldg_bldg_code
                   and g3.gorsdav_pk_parenttab(+) = s.slbbldg_bldg_code
                   and gorguid_guid = :GUID'''
        sqlQuery = sessionFactory.currentSession.createSQLQuery(verifyQuery)
        sqlQuery.setString("GUID", guidList[0])
        def verifyResults = sqlQuery.list()
        assertEquals 1, verifyResults.size()
        verifyResults.each { row ->
            assertEquals 750, row[0].toInteger()
            assertEquals 'BIG RED TREE', row[1]
            assertEquals 500, row[2].toInteger()
            assertEquals "2012-03-25", new SimpleDateFormat("yyyy-MM-dd").format(row[3])
        }

        //////  Test DELETE method   //////

        method = "DELETE"
        requestContent = """{"id":""" + "\"${guidList[0]}\"" + ""","code":"TECH","title":"Technology Hall","hedmCapacity":999,"hedmConstructionDate":"2017-09-15","hedmLandmark":"ANOTHER BIG TREE","hedmRoomCount":999}"""
        responseContent = requestContent
        expectedContent = updatedContent // a delete should not change extension values

        request = newMockHttpServletRequest(method, mediaType, requestContent)

        extensionProcessResult = extensionProcessCompositeService.applyExtensions(resourceName, request, null, responseContent, false)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content
        assertTrue extensionProcessResult.extensionsApplied

        verifyResults = sqlQuery.list()
        assertEquals 1, verifyResults.size()
        verifyResults.each { row ->
            assertEquals 750, row[0].toInteger()
            assertEquals 'BIG RED TREE', row[1]
            assertEquals 500, row[2].toInteger()
            assertEquals "2012-03-25", new SimpleDateFormat("yyyy-MM-dd").format(row[3])
        }

        //////  Test POST method   //////

        method = "POST"
        requestContent = originalContent
        responseContent = requestContent
        expectedContent = requestContent

        request = newMockHttpServletRequest(method, mediaType, requestContent)

        extensionProcessResult = extensionProcessCompositeService.applyExtensions(resourceName, request, null, responseContent, false)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content
        assertTrue extensionProcessResult.extensionsApplied

        verifyResults = sqlQuery.list()
        assertEquals 1, verifyResults.size()
        verifyResults.each { row ->
            assertEquals 150, row[0].toInteger()
            assertEquals 'SMALL RED TREE', row[1]
            assertEquals 10, row[2].toInteger()
            assertEquals "2013-06-24", new SimpleDateFormat("yyyy-MM-dd").format(row[3])
        }

        //////  Test POST QAPI method - expect no change to database when isQapi=true   //////

        method = "POST"
        requestContent = """{"id":""" + "\"${guidList[0]}\"" + ""","code":"TECH","title":"Technology Hall","hedmCapacity":456,"hedmConstructionDate":"2017-09-15","hedmLandmark":"A LITTLE TREE","hedmRoomCount":789}"""
        responseContent = originalContent
        expectedContent = originalContent

        request = newMockHttpServletRequest(method, mediaType, requestContent)

        extensionProcessResult = extensionProcessCompositeService.applyExtensions(resourceName, request, null, responseContent, true)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content
        assertTrue extensionProcessResult.extensionsApplied

        verifyResults = sqlQuery.list()
        assertEquals 1, verifyResults.size()
        verifyResults.each { row ->
            assertEquals 150, row[0].toInteger()
            assertEquals 'SMALL RED TREE', row[1]
            assertEquals 10, row[2].toInteger()
            assertEquals "2013-06-24", new SimpleDateFormat("yyyy-MM-dd").format(row[3])
        }
    }


    @Test
    void testMaritalStatuses() {
        def resourceName = "marital-statuses"
        def mediaType = "application/vnd.hedtech.integration.v6+json"

        def guidList = []
        def guidQuery = "select gorguid_guid from gorguid where gorguid_ldm_name = 'marital-status' and gorguid_domain_key = 'S'"
        def sqlQuery = sessionFactory.currentSession.createSQLQuery(guidQuery)
        def guidResults = sqlQuery.list()
        assertEquals 1, guidResults.size()
        guidResults.each { row ->
            guidList.add(row)
        }

        //////  Test GET method  //////

        // pre-set the expected values
        def updateQuery = "update stvmrtl set stvmrtl_fa_conv_code = 'A', stvmrtl_edi_equiv = 'B' where stvmrtl_code = 'S'"
        sqlQuery = sessionFactory.currentSession.createSQLQuery(updateQuery)
        int updateCount = sqlQuery.executeUpdate()
        assertEquals 1, updateCount

        def method = "GET"
        def requestContent = null
        def responseContent = """{"id":""" + "\"${guidList[0]}\"" + ""","code":"S","title":"Single","maritalCategory":"single"}"""
        def expectedContent = """{"id":""" + "\"${guidList[0]}\"" + ""","code":"S","title":"Single","maritalCategory":"single","hedmEdiEquivalent":"B","hedmFinanceConversion":"A"}"""

        def request = newMockHttpServletRequest(method, mediaType, requestContent)

        ExtensionProcessResult extensionProcessResult = extensionProcessCompositeService.applyExtensions(resourceName, request, null, responseContent, false)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content
        assertTrue extensionProcessResult.extensionsApplied

        def originalContent = expectedContent

        //////  Test PUT method  //////

        method = "PUT"
        requestContent = """{"id":""" + "\"${guidList[0]}\"" + ""","code":"S","title":"Single","maritalCategory":"single","hedmEdiEquivalent":"Y","hedmFinanceConversion":"X"}"""
        responseContent = requestContent
        expectedContent = requestContent

        def updatedContent = expectedContent

        request = newMockHttpServletRequest(method, mediaType, requestContent)

        extensionProcessResult = extensionProcessCompositeService.applyExtensions(resourceName, request, null, responseContent, false)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content
        assertTrue extensionProcessResult.extensionsApplied

        def verifyQuery = '''
                select stvmrtl_fa_conv_code,
                       stvmrtl_edi_equiv
                  from stvmrtl
                 where stvmrtl_code = 'S\''''
        sqlQuery = sessionFactory.currentSession.createSQLQuery(verifyQuery)
        def verifyResults = sqlQuery.list()
        assertEquals 1, verifyResults.size()
        verifyResults.each { row ->
            assertEquals 'X', row[0]
            assertEquals 'Y', row[1]
        }

        //////  Test DELETE method   //////

        method = "DELETE"
        requestContent = """{"id":""" + "\"${guidList[0]}\"" + ""","code":"S","title":"Single","maritalCategory":"single","hedmEdiEquivalent":"1","hedmFinanceConversion":"2"}"""
        responseContent = requestContent
        expectedContent = updatedContent // a delete should not change extension values

        request = newMockHttpServletRequest(method, mediaType, requestContent)

        extensionProcessResult = extensionProcessCompositeService.applyExtensions(resourceName, request, null, responseContent, false)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content
        assertTrue extensionProcessResult.extensionsApplied

        verifyResults = sqlQuery.list()
        assertEquals 1, verifyResults.size()
        verifyResults.each { row ->
            assertEquals 'X', row[0]
            assertEquals 'Y', row[1]
        }

        //////  Test POST method   //////

        method = "POST"
        requestContent = originalContent
        responseContent = requestContent
        expectedContent = requestContent

        request = newMockHttpServletRequest(method, mediaType, requestContent)

        extensionProcessResult = extensionProcessCompositeService.applyExtensions(resourceName, request, null, responseContent, false)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content
        assertTrue extensionProcessResult.extensionsApplied

        verifyResults = sqlQuery.list()
        assertEquals 1, verifyResults.size()
        verifyResults.each { row ->
            assertEquals 'A', row[0]
            assertEquals 'B', row[1]
        }

        //////  Test POST QAPI method - expect no change to database when isQapi=true   //////

        method = "POST"
        requestContent = """{"id":""" + "\"${guidList[0]}\"" + ""","code":"S","title":"Single","maritalCategory":"single","hedmEdiEquivalent":"Y","hedmFinanceConversion":"X"}"""
        responseContent = originalContent
        expectedContent = originalContent

        request = newMockHttpServletRequest(method, mediaType, requestContent)

        extensionProcessResult = extensionProcessCompositeService.applyExtensions(resourceName, request, null, responseContent, true)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content
        assertTrue extensionProcessResult.extensionsApplied

        verifyResults = sqlQuery.list()
        assertEquals 1, verifyResults.size()
        verifyResults.each { row ->
            assertEquals 'A', row[0]
            assertEquals 'B', row[1]
        }
    }


    @Test
    void testInstructionalMethods() {
        def resourceName = "instructional-methods"
        def mediaType = "application/vnd.hedtech.integration.v6+json"

        def guidList = []
        def guidQuery = "select gorguid_guid from gorguid where gorguid_ldm_name = 'instructional-methods' and gorguid_domain_key = 'DC'"
        def sqlQuery = sessionFactory.currentSession.createSQLQuery(guidQuery)
        def guidResults = sqlQuery.list()
        assertEquals 1, guidResults.size()
        guidResults.each { row ->
            guidList.add(row)
        }

        //////  Test GET method  //////

        def method = "GET"
        def requestContent = null
        def responseContent = """{"id":""" + "\"${guidList[0]}\"" + ""","abbreviation":"DC","title":"Just another test code"}"""
        def expectedContent = """{"id":""" + "\"${guidList[0]}\"" + ""","abbreviation":"DC","title":"Just another test code","hedmExternalData":{"hedmCode":"A12365427","hedmHistory":[{"date":"2002-01-13","status":"FT","level":7},{"date":"2000-12-06","status":"PT","level":5}]},"hedmInstructionalMethodCode":"WLB","hedmDuration":21,"hedmEndDate":"2008-02-09","hedmInstructor":"Smith","hedmStartDate":"2000-12-06"}"""

        def request = newMockHttpServletRequest(method, mediaType, requestContent)

        ExtensionProcessResult extensionProcessResult = extensionProcessCompositeService.applyExtensions(resourceName, request, null, responseContent, false)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content
        assertTrue extensionProcessResult.extensionsApplied

        def originalContent = expectedContent

        //////  Test PUT method  //////

        method = "PUT"
        requestContent = """{"id":""" + "\"${guidList[0]}\"" + ""","abbreviation":"DC","title":"Just another test code","hedmExternalData":{"hedmCode":"A1236542789","hedmHistory":{"hasHistory":false}},"hedmInstructionalMethodCode":"TR","hedmDuration":23,"hedmEndDate":"2008-03-11","hedmInstructor":"Smithy","hedmStartDate":"2000-12-10"}"""
        responseContent = requestContent
        expectedContent = requestContent

        def updatedContent = expectedContent

        request = newMockHttpServletRequest(method, mediaType, requestContent)

        extensionProcessResult = extensionProcessCompositeService.applyExtensions(resourceName, request, null, responseContent, false)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content
        assertTrue extensionProcessResult.extensionsApplied

        def verifyQuery = '''
                select stvschd_insm_code,
                       sys.anydata.accessNumber(g1.gorsdav_value) as HEDM_DURATION,
                       sys.anydata.accessVarchar2(g2.gorsdav_value) as HEDM_INSTRUCTOR,
                       sys.anydata.accessDate(g3.gorsdav_value) as HEDM_START_DATE,
                       sys.anydata.accessDate(g4.gorsdav_value) as HEDM_END_DATE,
                       sys.anydata.accessVarchar2(g5.gorsdav_value) as HEDM_EXTERNAL_CODE,
                       sys.anydata.accessVarchar2(g6.gorsdav_value) as HEDM_EXTERNAL_HISTORY
                  from gorguid g, stvschd s, gorsdav g1, gorsdav g2, gorsdav g3, gorsdav g4, gorsdav g5, gorsdav g6
                 where gorguid_ldm_name = 'instructional-methods'
                   and g.gorguid_domain_surrogate_id = s.stvschd_surrogate_id
                   and g1.gorsdav_table_name(+) = 'STVSCHD'
                   and g2.gorsdav_table_name(+) = 'STVSCHD'
                   and g3.gorsdav_table_name(+) = 'STVSCHD'
                   and g4.gorsdav_table_name(+) = 'STVSCHD'
                   and g5.gorsdav_table_name(+) = 'STVSCHD'
                   and g6.gorsdav_table_name(+) = 'STVSCHD'
                   and g1.gorsdav_attr_name(+) = 'HEDM_DURATION'
                   and g2.gorsdav_attr_name(+) = 'HEDM_INSTRUCTOR'
                   and g3.gorsdav_attr_name(+) = 'HEDM_START_DATE'
                   and g4.gorsdav_attr_name(+) = 'HEDM_END_DATE'
                   and g5.gorsdav_attr_name(+) = 'HEDM_EXTERNAL_CODE'
                   and g6.gorsdav_attr_name(+) = 'HEDM_EXTERNAL_HISTORY'
                   and g1.gorsdav_pk_parenttab(+) = s.stvschd_code
                   and g2.gorsdav_pk_parenttab(+) = s.stvschd_code
                   and g3.gorsdav_pk_parenttab(+) = s.stvschd_code
                   and g4.gorsdav_pk_parenttab(+) = s.stvschd_code
                   and g5.gorsdav_pk_parenttab(+) = s.stvschd_code
                   and g6.gorsdav_pk_parenttab(+) = s.stvschd_code
                   and gorguid_guid = :GUID'''
        sqlQuery = sessionFactory.currentSession.createSQLQuery(verifyQuery)
        sqlQuery.setString("GUID", guidList[0])
        def verifyResults = sqlQuery.list()
        assertEquals 1, verifyResults.size()
        verifyResults.each { row ->
            assertEquals 'TR', row[0]
            assertEquals 23, row[1].toInteger()
            assertEquals 'Smithy', row[2]
            assertEquals "2000-12-10", new SimpleDateFormat("yyyy-MM-dd").format(row[3])
            assertEquals "2008-03-11", new SimpleDateFormat("yyyy-MM-dd").format(row[4])
            assertEquals 'A1236542789', row[5]
            assertEquals '{"hasHistory":false}', row[6]
        }

        //////  Test DELETE method   //////

        method = "DELETE"
        requestContent = """{"id":""" + "\"${guidList[0]}\"" + ""","abbreviation":"DC","title":"Just another test code","hedmExternalData":{"hedmCode":"A123","hedmHistory":{"hasHistory":true}},"hedmInstructionalMethodCode":"WEB","hedmDuration":25,"hedmEndDate":"2009-03-11","hedmInstructor":"Smithers","hedmStartDate":"2003-12-10"}"""
        responseContent = requestContent
        expectedContent = updatedContent // a delete should not change extension values

        request = newMockHttpServletRequest(method, mediaType, requestContent)

        extensionProcessResult = extensionProcessCompositeService.applyExtensions(resourceName, request, null, responseContent, false)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content
        assertTrue extensionProcessResult.extensionsApplied

        verifyResults = sqlQuery.list()
        assertEquals 1, verifyResults.size()
        verifyResults.each { row ->
            assertEquals 'TR', row[0]
            assertEquals 23, row[1].toInteger()
            assertEquals 'Smithy', row[2]
            assertEquals "2000-12-10", new SimpleDateFormat("yyyy-MM-dd").format(row[3])
            assertEquals "2008-03-11", new SimpleDateFormat("yyyy-MM-dd").format(row[4])
            assertEquals 'A1236542789', row[5]
            assertEquals '{"hasHistory":false}', row[6]
        }

        //////  Test POST method   //////

        method = "POST"
        requestContent = originalContent
        responseContent = requestContent
        // must adjust expected content for re-parsing of raw json text
        expectedContent = requestContent.
                replaceAll('"date":"2002-01-13","status":"FT","level":7','"date":"2002-01-13","level":7,"status":"FT"').
                replaceAll('"date":"2000-12-06","status":"PT","level":5','"date":"2000-12-06","level":5,"status":"PT"')

        //adjusting expected content above

        request = newMockHttpServletRequest(method, mediaType, requestContent)

        extensionProcessResult = extensionProcessCompositeService.applyExtensions(resourceName, request, null, responseContent, false)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content
        assertTrue extensionProcessResult.extensionsApplied

        verifyResults = sqlQuery.list()
        assertEquals 1, verifyResults.size()
        verifyResults.each { row ->
            assertEquals 'WLB', row[0]
            assertEquals 21, row[1].toInteger()
            assertEquals 'Smith', row[2]
            assertEquals "2000-12-06", new SimpleDateFormat("yyyy-MM-dd").format(row[3])
            assertEquals "2008-02-09", new SimpleDateFormat("yyyy-MM-dd").format(row[4])
            assertEquals 'A12365427', row[5]
            assertEquals '[{"date":"2002-01-13","level":7,"status":"FT"},{"date":"2000-12-06","level":5,"status":"PT"}]', row[6]
        }
    }


    @Test
    void testApiVersionInRepresentationConfig() {
        def resourceName = "buildings"
        def mediaType = "application/json"
        def apiVersion = new BasicApiVersionParser().parseMediaType(resourceName, "application/vnd.hedtech.integration.v6+json")

        def guidList = []
        def guidQuery = "select gorguid_guid from gorguid where gorguid_ldm_name = 'buildings' and gorguid_domain_key = 'TECH'"
        def sqlQuery = sessionFactory.currentSession.createSQLQuery(guidQuery)
        def guidResults = sqlQuery.list()
        assertEquals 1, guidResults.size()
        guidResults.each { row ->
            guidList.add(row)
        }

        //////  Test GET method  //////

        def method = "GET"
        def requestContent = null
        def responseContent = """{"id":""" + "\"${guidList[0]}\"" + ""","code":"TECH","title":"Technology Hall"}"""
        def expectedContent = """{"id":""" + "\"${guidList[0]}\"" + ""","code":"TECH","title":"Technology Hall","hedmCapacity":150,"hedmConstructionDate":"2013-06-24","hedmLandmark":"SMALL RED TREE","hedmRoomCount":10}"""

        def request = newMockHttpServletRequest(method, mediaType, requestContent, apiVersion)

        ExtensionProcessResult extensionProcessResult = extensionProcessCompositeService.applyExtensions(resourceName, request, null, responseContent, false)
        assertNotNull extensionProcessResult
        assertEquals expectedContent, extensionProcessResult.content
        assertTrue extensionProcessResult.extensionsApplied
    }


    private MockHttpServletRequest newMockHttpServletRequest(method, mediaType, content, apiVersion = null) {
        MockHttpServletRequest request = new MockHttpServletRequest()
        request.setCharacterEncoding("UTF-8")
        request.setMethod(method)
        RepresentationConfig representationConfig = new RepresentationConfig()
        representationConfig.mediaType = mediaType
        if (apiVersion) {
            representationConfig.apiVersion = apiVersion
        }
        request.setAttribute(RepresentationRequestAttributes.RESPONSE_REPRESENTATION, representationConfig)
        if (content) {
            request.setContentType("application/json")
            request.setContent(content.getBytes())
        }
        return request
    }

}
