package net.hedtech.banner.restfulapi.testing

import grails.converters.JSON

class FooFunctionalSpec extends BaseFunctionalSpec {

    private static final String RESOURCE_NAME = "foo"


    def "list - version 1 - without pagination"() {
        when:
        get("${RESTFUL_API_BASE_URL}/${RESOURCE_NAME}") {
            headers['Accept'] = 'application/vnd.hedtech.v1+json'
            headers['Authorization'] = authHeader()
        }

        then:
        200 == response.status
        null == responseHeader('X-Status-Reason')
        'application/json' == response.contentType
        'application/vnd.hedtech.v1+json' == responseHeader("X-hedtech-Media-Type")
        "List of foo resources" == responseHeader('X-hedtech-message')
        def numberOfRecordsInResponse = JSON.parse(response.text).size()
        0 < numberOfRecordsInResponse
        0 == responseHeader('X-hedtech-pageOffset').toInteger()
    }

}
