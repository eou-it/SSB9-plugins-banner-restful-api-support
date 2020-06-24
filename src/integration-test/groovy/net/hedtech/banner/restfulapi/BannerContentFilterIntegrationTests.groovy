/*******************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.ContentFilter
import net.hedtech.restfulapi.ContentFilterFields
import net.hedtech.restfulapi.ContentFilterResult

import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Test class for BannerContentFilter
 */

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import static groovy.test.GroovyAssert.*

@Rollback
@Integration
class BannerContentFilterIntegrationTests extends BannerFilterConfigTestData {

    ContentFilter restContentFilter
    ContentFilterFields restContentFilterFields


    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        teardownTestData()
        restContentFilterFields = new BannerContentFilterFields()
        restContentFilterFields.sessionFactory = sessionFactory
        restContentFilter = new BannerContentFilter()
        restContentFilter.restContentFilterFields = restContentFilterFields
    }


    @After
    public void tearDown() {
        teardownTestData()
        super.tearDown()
    }

    // Since BannerContentFilter just extends the restful-api BasicContentFilter, we just
    // have to verify that everything is wired up correctly through spring, as all the
    // actual pattern matching logic is covered by the BasicContentFilter tests.

    @Test
    void testUsingSimpleFilterWithMultipleChanges() {
        def testData = [
                [resourceName: 'my-resource', fieldPattern: 'description', seqno: 1, statusInd: 'A', userPattern: '*'],
                [resourceName: 'my-resource', fieldPattern: 'code', seqno: 1, statusInd: 'A', userPattern: '*']
        ]
        createTestData(testData)
        assertEquals 2, verifyCount()

        def jsonText = """
{ "code": "201410",
  "description": "Fall 2013",
  "status": "Active"
}"""

        def filteredJsonText = """{"status":"Active"}"""

        ContentFilterResult jsonTextResult = restContentFilter.applyFilter(
                "my-resource",
                jsonText,
                "application/json")

        assertTrue jsonTextResult.isPartial
        assertEquals filteredJsonText, jsonTextResult.content
    }

}
