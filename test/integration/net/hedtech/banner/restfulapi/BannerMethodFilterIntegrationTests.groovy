/*******************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.MethodFilter
import net.hedtech.restfulapi.Methods

import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Test class for BannerMethodFilter
 */
class BannerMethodFilterIntegrationTests extends BannerFilterConfigTestData {

    MethodFilter restMethodFilter


    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        teardownTestData()
        restMethodFilter = new BannerMethodFilter()
        restMethodFilter.sessionFactory = sessionFactory
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
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.LIST)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.SHOW)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.CREATE)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.UPDATE)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.DELETE)
    }


    @Test
    void testWithNoMethodsSpecified() {
        def testData = [
            [resourceName: 'my-resource', fieldPattern: '*', seqno: 1, statusInd: 'A', userPattern: '*']
        ]
        createTestData(testData)
        assertEquals 1, verifyCount()

        // test resource
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.LIST)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.SHOW)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.CREATE)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.UPDATE)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.DELETE)
    }


    @Test
    void testWithOneMethodSpecified() {
        def testData = [
            [resourceName: 'my-resource', fieldPattern: '*', methodsNotAllowed: 'C', seqno: 1, statusInd: 'A', userPattern: '*']
        ]
        createTestData(testData)
        assertEquals 1, verifyCount()

        // test resource
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.LIST)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.SHOW)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.CREATE)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.UPDATE)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.DELETE)

        // test another resource name not found
        assertFalse restMethodFilter.isMethodNotAllowed("another-resource", Methods.LIST)
        assertFalse restMethodFilter.isMethodNotAllowed("another-resource", Methods.SHOW)
        assertFalse restMethodFilter.isMethodNotAllowed("another-resource", Methods.CREATE)
        assertFalse restMethodFilter.isMethodNotAllowed("another-resource", Methods.UPDATE)
        assertFalse restMethodFilter.isMethodNotAllowed("another-resource", Methods.DELETE)
    }


    @Test
    void testWithMultipleMethodsSpecified() {
        def testData = [
            [resourceName: 'my-resource', fieldPattern: '*', methodsNotAllowed: 'CRUD', seqno: 1, statusInd: 'A', userPattern: '*']
        ]
        createTestData(testData)
        assertEquals 1, verifyCount()

        // test resource
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.LIST)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.SHOW)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.CREATE)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.UPDATE)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.DELETE)

        // test another resource name not found
        assertFalse restMethodFilter.isMethodNotAllowed("another-resource", Methods.LIST)
        assertFalse restMethodFilter.isMethodNotAllowed("another-resource", Methods.SHOW)
        assertFalse restMethodFilter.isMethodNotAllowed("another-resource", Methods.CREATE)
        assertFalse restMethodFilter.isMethodNotAllowed("another-resource", Methods.UPDATE)
        assertFalse restMethodFilter.isMethodNotAllowed("another-resource", Methods.DELETE)
    }


    @Test
    void testWithDuplicateMethodsSpecified() {
        def testData = [
            [resourceName: 'my-resource', fieldPattern: '*', methodsNotAllowed: 'CRUDCUD', seqno: 1, statusInd: 'A', userPattern: '*']
        ]
        createTestData(testData)
        assertEquals 1, verifyCount()

        // test resource
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.LIST)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.SHOW)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.CREATE)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.UPDATE)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.DELETE)

        // test another resource name not found
        assertFalse restMethodFilter.isMethodNotAllowed("another-resource", Methods.LIST)
        assertFalse restMethodFilter.isMethodNotAllowed("another-resource", Methods.SHOW)
        assertFalse restMethodFilter.isMethodNotAllowed("another-resource", Methods.CREATE)
        assertFalse restMethodFilter.isMethodNotAllowed("another-resource", Methods.UPDATE)
        assertFalse restMethodFilter.isMethodNotAllowed("another-resource", Methods.DELETE)
    }


    @Test
    void testWithInvalidMethodsSpecified() {
        def testData = [
            [resourceName: 'my-resource', fieldPattern: '*', methodsNotAllowed: 'XYZ', seqno: 1, statusInd: 'A', userPattern: '*']
        ]
        createTestData(testData)
        assertEquals 1, verifyCount()

        // test resource
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.LIST)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.SHOW)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.CREATE)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.UPDATE)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.DELETE)
    }


    @Test
    void testFieldPatternsByUser() {
        def testData = [
            [resourceName: 'my-resource', fieldPattern: '*', methodsNotAllowed: 'CD', seqno: 1, statusInd: 'A', userPattern: 'OTHER_USER'],
            [resourceName: 'my-resource', fieldPattern: '*', methodsNotAllowed: 'R', seqno: 2, statusInd: 'A', userPattern: 'GRAILS_USER']
        ]
        createTestData(testData)
        assertEquals 2, verifyCount()

        // test resource
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.LIST)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.SHOW)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.CREATE)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.UPDATE)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.DELETE)
    }


    @Test
    void testFieldPatternsByGroup() {
        // groups and users within those groups are created automatically
        // when referenced by the group irregardless of the field pattern
        // for which they are specified; the API_TEST1_FPBR group will contain
        // all 3 users which are correlated to all 3 field patterns
        def testData = [
            [resourceName: 'my-resource', fieldPattern: '*', methodsNotAllowed: 'CU', seqno: 1, statusInd: 'A', userPattern: 'OTHER_USER:API_TEST1_FPBR'],
            [resourceName: 'my-resource', fieldPattern: 'code', seqno: 1, statusInd: 'A', userPattern: 'GRAILS_USER:API_TEST1_FPBR'],
            [resourceName: 'my-resource', fieldPattern: 'desc', seqno: 1, statusInd: 'A', userPattern: 'ANOTHER_USER:API_TEST1_FPBR']
        ]
        createTestData(testData)
        assertEquals 3, verifyCount()

        // test resource
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.LIST)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.SHOW)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.CREATE)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.UPDATE)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.DELETE)
    }


    @Test
    void testFieldPatternsByOrderedGroupCode() {
        // use the alphabetically first group if a person is referenced
        // by multiple groups for the same field pattern
        def testData = [
            [resourceName: 'my-resource', fieldPattern: '*', methodsNotAllowed: 'C', seqno: 1, statusInd: 'I', userPattern: 'GRAILS_USER:API_TEST2_FPBR'],
            [resourceName: 'my-resource', fieldPattern: '*', methodsNotAllowed: 'C', seqno: 2, statusInd: 'A', userPattern: 'GRAILS_USER:API_TEST1_FPBR']
        ]
        createTestData(testData)
        assertEquals 2, verifyCount()

        // test resource
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.CREATE)

        // add another entry to cause field pattern to be removed
        testData.add(
            [resourceName: 'my-resource', fieldPattern: '*', methodsNotAllowed: 'C', seqno: 3, statusInd: 'I', userPattern: 'GRAILS_USER:API_TEST0_FPBR']
        )
        createTestData(testData)
        assertEquals 3, verifyCount()

        // test resource
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.CREATE)
    }


    @Test
    void testFieldPatternsPrioritization() {
        def testData = [
            [resourceName: 'my-resource', fieldPattern: '*', methodsNotAllowed: 'CUD', seqno: 1, statusInd: 'A', userPattern: '*']
        ]
        createTestData(testData)
        assertEquals 1, verifyCount()

        // test resource
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.LIST)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.SHOW)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.CREATE)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.UPDATE)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.DELETE)

        // add another entry to show groups can override all users
        testData.add(
            [resourceName: 'my-resource', fieldPattern: '*', methodsNotAllowed: 'C', seqno: 2, statusInd: 'I', userPattern: 'GRAILS_USER:API_TEST0_FPBR']
        )
        createTestData(testData)
        assertEquals 2, verifyCount()

        // test resource
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.LIST)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.SHOW)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.CREATE)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.UPDATE)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.DELETE)

        // add another entry to show individual user can override groups all users
        testData.add(
            [resourceName: 'my-resource', fieldPattern: '*', methodsNotAllowed: 'C', seqno: 3, statusInd: 'A', userPattern: 'GRAILS_USER'],
        )
        createTestData(testData)
        assertEquals 3, verifyCount()

        // test resource
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.LIST)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.SHOW)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.CREATE)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.UPDATE)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.DELETE)
    }


    @Test
    void testEmsApiUserNotAllowedMethods() {
        assertEquals 0, verifyCount()

        // set the EMS API user for this test
        updateIntegrationConfiguration("EMS-ETHOS-INTEGRATION", "EMS.API.USERNAME", "GRAILS_USER")

        // test EMS API user with no matching resource is denied CUD methods
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.LIST)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.SHOW)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.CREATE)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.UPDATE)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.DELETE)

        def testData = [
            [resourceName: 'my-resource', fieldPattern: '*', methodsNotAllowed: null, seqno: 1, statusInd: 'A', userPattern: '*']
        ]
        createTestData(testData)
        assertEquals 1, verifyCount()

        // test EMS API user with matching resource allowing full CRUD is still denied CUD methods
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.LIST)
        assertFalse restMethodFilter.isMethodNotAllowed("my-resource", Methods.SHOW)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.CREATE)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.UPDATE)
        assertTrue restMethodFilter.isMethodNotAllowed("my-resource", Methods.DELETE)
    }


    @Test
    void testMissingSessionFactoryInjection() {
        restMethodFilter.sessionFactory = null
        try {
            restMethodFilter.isMethodNotAllowed("my-resource", Methods.CREATE)
            throw new RuntimeException("Expected an AssertionError")
        } catch(AssertionError e) {
            // ignore
        }
    }

}
