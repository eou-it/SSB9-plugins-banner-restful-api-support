/* ****************************************************************************
Copyright 2013-2018 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.banner.restfulapi

import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletWebRequest
import spock.lang.Specification

class ApiErrorFactorySpec extends Specification {


    def "Test create nulls"() {
        setup:

        when:
        ApiErrorFactory.create(null, null, null, null, null, null)

        then:
        thrown IllegalArgumentException
    }

    def "Test create wrong versions"() {
        setup:

        when:
        ApiErrorFactory.create("error", null, null, null, null, null)

        then:
        thrown IllegalArgumentException
    }

    def "Test create version v2 no message"() {
        setup:
        RequestAttributes mockRequest = new ServletWebRequest(new MockHttpServletRequest("GET", "/test"))
        RequestContextHolder.setRequestAttributes(mockRequest)

        when:
        ApiErrorFactory.create(ApiErrorFactory.V2_ERROR_TYPE, null, null, null, null, null)

        then:
        thrown IllegalArgumentException
    }

    def "Test create version v2 minimal"() {
        setup:
        RequestAttributes mockRequest = new ServletWebRequest(new MockHttpServletRequest("GET", "/test"))
        RequestContextHolder.setRequestAttributes(mockRequest)
        when:
        def error = ApiErrorFactory.create(ApiErrorFactory.V2_ERROR_TYPE, null, null, null, null, "message")

        then:
        error["errors"][0]["id"] == null
        error["errors"][0]["sourceId"] == null
        error["errors"][0]["code"] == null
        error["errors"][0]["description"] == null
        error["errors"][0]["message"].equals( "message" )
    }

    def "Test create version v2"() {
        setup:
        RequestAttributes mockRequest = new ServletWebRequest(new MockHttpServletRequest("GET", "/test"))
        RequestContextHolder.setRequestAttributes(mockRequest)
        when:
        def error = ApiErrorFactory.create(ApiErrorFactory.V2_ERROR_TYPE, "someId", "someSourceId", "someCode", "Desc", "message")

        then:
        error["errors"][0]["id"].equals( "someId" )
        error["errors"][0]["sourceId"].equals( "someSourceId" )
        error["errors"][0]["code"].equals( "someCode" )
        error["errors"][0]["description"].equals( "Desc" )
        error["errors"][0]["message"].equals( "message" )
    }

}
