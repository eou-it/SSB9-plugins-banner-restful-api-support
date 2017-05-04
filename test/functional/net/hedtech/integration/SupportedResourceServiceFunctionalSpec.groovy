/* ****************************************************************************
Copyright 2017 Ellucian Company L.P. and its affiliates.
******************************************************************************/

package net.hedtech.integration

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
        def representation1b = resource1.representations.find { it."X-Media-Type" == "application/vnd.hedtech.v1+json" }
        null != representation1b
        ["get","post","put","delete"] == representation1b.methods
        def resource2 = resources.find { it.name == "resources" }
        null != resource2
        3 == resource2.representations.size()
        def representation2a = resource2.representations.find { it."X-Media-Type" == "application/json" }
        null != representation2a
        ["get"] == representation2a.methods
        def representation2b = resource2.representations.find { it."X-Media-Type" == "application/vnd.hedtech.v1+json" }
        null != representation2b
        [] == representation2b.methods
        def representation2c = resource2.representations.find { it."X-Media-Type" == "application/vnd.hedtech.v2+json" }
        null != representation2c
        ["get"] == representation2c.methods
    }
}
