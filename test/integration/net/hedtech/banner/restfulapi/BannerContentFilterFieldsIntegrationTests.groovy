/*******************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.ContentFilterFields

import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Test class for BannerContentFilterFields
 */
class BannerContentFilterFieldsIntegrationTests extends BannerFilterConfigTestData {

    ContentFilterFields restContentFilterFields


    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        teardownTestData()
        restContentFilterFields = new BannerContentFilterFields()
        restContentFilterFields.sessionFactory = sessionFactory
    }


    @After
    public void tearDown() {
        teardownTestData()
        super.tearDown()
    }


    @Test
    void testResourceNameNotFound() {
        assertEquals 0, verifyCount()

        // test resource
        List fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 0, fieldPatterns.size()
    }


    @Test
    void testWithOneFieldPattern() {
        def testData = [
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 1, displayInd: 'N', userPattern: '*']
        ]
        createTestData(testData)
        assertEquals 1, verifyCount()

        // test resource
        List fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 1, fieldPatterns.size()
        assertEquals "name", fieldPatterns.get(0)

        // test another resource name not found
        fieldPatterns = restContentFilterFields.retrieveFieldPatterns("another-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 0, fieldPatterns.size()
    }


    @Test
    void testWithMultipleFieldPatternsSorted() {
        def testData = [
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 1, displayInd: 'N', userPattern: '*'],
            [resourceName: 'my-resource', fieldPattern: 'code', seqno: 1, displayInd: 'N', userPattern: '*'],
            [resourceName: 'my-resource', fieldPattern: 'desc', seqno: 1, displayInd: 'N', userPattern: '*']
        ]
        createTestData(testData)
        assertEquals 3, verifyCount()

        // test resource
        List fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 3, fieldPatterns.size()
        assertEquals "code", fieldPatterns.get(0)
        assertEquals "desc", fieldPatterns.get(1)
        assertEquals "name", fieldPatterns.get(2)

        // test another resource name not found
        fieldPatterns = restContentFilterFields.retrieveFieldPatterns("another-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 0, fieldPatterns.size()
    }


    @Test
    void testWithDuplicateFieldPatternsRemoved() {
        def testData = [
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 1, displayInd: 'N', userPattern: '*'],
            [resourceName: 'my-resource', fieldPattern: 'code', seqno: 1, displayInd: 'N', userPattern: '*'],
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 2, displayInd: 'N', userPattern: '*'],
            [resourceName: 'my-resource', fieldPattern: 'desc', seqno: 1, displayInd: 'N', userPattern: '*'],
            [resourceName: 'my-resource', fieldPattern: 'code', seqno: 2, displayInd: 'N', userPattern: '*']
        ]
        createTestData(testData)
        assertEquals 5, verifyCount()

        // test resource
        List fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 3, fieldPatterns.size()
        assertEquals "code", fieldPatterns.get(0)
        assertEquals "desc", fieldPatterns.get(1)
        assertEquals "name", fieldPatterns.get(2)

        // test another resource name not found
        fieldPatterns = restContentFilterFields.retrieveFieldPatterns("another-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 0, fieldPatterns.size()
    }


    @Test
    void testFieldPatternsByUser() {
        def testData = [
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 1, displayInd: 'N', userPattern: 'OTHER_USER'],
            [resourceName: 'my-resource', fieldPattern: 'code', seqno: 1, displayInd: 'N', userPattern: 'GRAILS_USER'],
            [resourceName: 'my-resource', fieldPattern: 'desc', seqno: 1, displayInd: 'N', userPattern: 'OTHER_USER']
        ]
        createTestData(testData)
        assertEquals 3, verifyCount()

        // test resource
        List fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 1, fieldPatterns.size()
        assertEquals "code", fieldPatterns.get(0)
    }


    @Test
    void testFieldPatternsByGroup() {
        // groups and users within those groups are created automatically
        // when referenced by the group irregardless of the field pattern
        // for which they are specified; the API_TEST1_FPBR group will contain
        // all 3 users which are correlated to all 3 field patterns
        def testData = [
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 1, displayInd: 'N', userPattern: 'OTHER_USER:API_TEST1_FPBR'],
            [resourceName: 'my-resource', fieldPattern: 'code', seqno: 1, displayInd: 'N', userPattern: 'GRAILS_USER:API_TEST1_FPBR'],
            [resourceName: 'my-resource', fieldPattern: 'desc', seqno: 1, displayInd: 'N', userPattern: 'ANOTHER_USER:API_TEST1_FPBR']
        ]
        createTestData(testData)
        assertEquals 3, verifyCount()

        // test resource
        List fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 3, fieldPatterns.size()
        assertEquals "code", fieldPatterns.get(0)
        assertEquals "desc", fieldPatterns.get(1)
        assertEquals "name", fieldPatterns.get(2)
    }


    @Test
    void testFieldPatternsByOrderedGroupCode() {
        // use the alphabetically first group if a person is referenced
        // by multiple groups for the same field pattern
        def testData = [
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 1, displayInd: 'Y', userPattern: 'GRAILS_USER:API_TEST2_FPBR'],
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 2, displayInd: 'N', userPattern: 'GRAILS_USER:API_TEST1_FPBR']
        ]
        createTestData(testData)
        assertEquals 2, verifyCount()

        // test resource
        List fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 1, fieldPatterns.size()
        assertEquals "name", fieldPatterns.get(0)

        // add another entry to cause field pattern to be removed
        testData.add(
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 3, displayInd: 'Y', userPattern: 'GRAILS_USER:API_TEST0_FPBR']
        )
        createTestData(testData)
        assertEquals 3, verifyCount()

        // test resource
        fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 0, fieldPatterns.size()
    }


    @Test
    void testFieldPatternsPrioritization() {
        def testData = [
            [resourceName: 'my-resource', fieldPattern: 'name', seqno: 1, displayInd: 'N', userPattern: '*'],
            [resourceName: 'my-resource', fieldPattern: 'code', seqno: 1, displayInd: 'N', userPattern: '*'],
            [resourceName: 'my-resource', fieldPattern: 'desc', seqno: 1, displayInd: 'N', userPattern: '*']
        ]
        createTestData(testData)
        assertEquals 3, verifyCount()

        // test resource
        List fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 3, fieldPatterns.size()
        assertEquals "code", fieldPatterns.get(0)
        assertEquals "desc", fieldPatterns.get(1)
        assertEquals "name", fieldPatterns.get(2)

        // add another entry to show groups can override all users
        testData.add(
                [resourceName: 'my-resource', fieldPattern: 'desc', seqno: 2, displayInd: 'Y', userPattern: 'GRAILS_USER:API_TEST0_FPBR']
        )
        createTestData(testData)
        assertEquals 4, verifyCount()

        // test resource
        fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 2, fieldPatterns.size()
        assertEquals "code", fieldPatterns.get(0)
        assertEquals "name", fieldPatterns.get(1)

        // add another entry to show individual user can override groups all users
        testData.add(
            [resourceName: 'my-resource', fieldPattern: 'desc', seqno: 3, displayInd: 'N', userPattern: 'GRAILS_USER'],
        )
        testData.add(
            [resourceName: 'my-resource', fieldPattern: 'code', seqno: 2, displayInd: 'Y', userPattern: 'GRAILS_USER']
        )
        createTestData(testData)
        assertEquals 6, verifyCount()

        // test resource
        fieldPatterns = restContentFilterFields.retrieveFieldPatterns("my-resource")
        assertNotNull fieldPatterns
        assertTrue fieldPatterns instanceof List
        assertEquals 2, fieldPatterns.size()
        assertEquals "desc", fieldPatterns.get(0)
        assertEquals "name", fieldPatterns.get(1)
    }


    @Test
    void testMissingSessionFactoryInjection() {
        restContentFilterFields.sessionFactory = null
        try {
            restContentFilterFields.retrieveFieldPatterns("my-resource")
            throw new RuntimeException("Expected an AssertionError")
        } catch(AssertionError e) {
            // ignore
        }
    }

}
