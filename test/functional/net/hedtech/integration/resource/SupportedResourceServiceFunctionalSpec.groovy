/* ****************************************************************************
Copyright 2017-2020 Ellucian Company L.P. and its affiliates.
******************************************************************************/

package net.hedtech.integration.resource

import grails.converters.JSON
import net.hedtech.banner.restfulapi.testing.BaseFunctionalSpec

class SupportedResourceServiceFunctionalSpec extends BaseFunctionalSpec {

    private static final String RESOURCE_NAME = "resources"


    def "list all resources"() {
        when:
        get("${RESTFUL_API_BASE_URL}/${RESOURCE_NAME}") {
            headers['Accept'] = 'application/json'
            headers['Authorization'] = authHeader()
        }

        then:
        200 == response.status
        'application/json' == response.contentType
        'application/json' == responseHeader("X-hedtech-Media-Type")
        "List of resource resources" == responseHeader('X-hedtech-message')
        "2" == responseHeader('X-hedtech-totalCount')
        def resources = JSON.parse(response.text)
        2 == resources.size()
        def resource1 = resources.find { it.name == "foo" }
        null != resource1
        2 == resource1.representations.size()
        def representation1a = resource1.representations.find { it."X-Media-Type" == "application/json" }
        null != representation1a
        ["get","post","put","delete"] == representation1a.methods
        null == representation1a.filters
        null == representation1a.namedQueries
        null == representation1a.deprecationNotice
        def representation1b = resource1.representations.find { it."X-Media-Type" == "application/vnd.hedtech.integration.v1+json" }
        null != representation1b
        ["get","post","put","delete"] == representation1b.methods
        null == representation1b.filters
        null == representation1b.namedQueries
        null == representation1b.deprecationNotice
        def resource2 = resources.find { it.name == "fooTestSupportedResources" }
        null != resource2
        3 == resource2.representations.size()
        def representation2a = resource2.representations.find { it."X-Media-Type" == "application/json" }
        null != representation2a
        ["get","post","put","delete"] == representation2a.methods
        ["filter1", "filter2"] == representation2a.filters
        [[name:"query1", filters:["query1-filter"]], [name:"query2", filters:["query2-filter"]]] == representation2a.namedQueries
        def deprecationNotice2a = representation2a.deprecationNotice
        "2015-03-12" == deprecationNotice2a.deprecatedOn
        "2017-09-01" == deprecationNotice2a.sunsetOn
        "Resource has properties not in use" == deprecationNotice2a.description
        def representation2b = resource2.representations.find { it."X-Media-Type" == "application/vnd.hedtech.integration.v1+json" }
        null != representation2b
        ["get"] == representation2b.methods
        ["filter1", "filter2"] == representation2b.filters
        [[name:"query1", filters:["query1-filter"]], [name:"query2", filters:["query2-filter"]]] == representation2b.namedQueries
        def deprecationNotice2b = representation2b.deprecationNotice
        "2015-03-12" == deprecationNotice2b.deprecatedOn
        "2017-09-01" == deprecationNotice2b.sunsetOn
        "Resource has properties not in use" == deprecationNotice2b.description
        def representation2c = resource2.representations.find { it."X-Media-Type" == "application/vnd.hedtech.integration.v2+json" }
        null != representation2c
        ["get","post","put","delete"] == representation2c.methods
        ["filter1", "filter2"] == representation2c.filters
        [[name:"query1", filters:["query1-filter"]], [name:"query2", filters:["query2-filter"]]] == representation2c.namedQueries
        def deprecationNotice2c = representation2c.deprecationNotice
        "2015-03-12" == deprecationNotice2c.deprecatedOn
        "2017-09-01" == deprecationNotice2c.sunsetOn
        "Resource has properties not in use" == deprecationNotice2c.description
    }
}
