/* ****************************************************************************
Copyright 2013-2018 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.banner.restfulapi

import net.hedtech.integration.exception.ExceptionCollector
import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandlerContext
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletWebRequest
import spock.lang.Specification

class BannerGeneralExceptionHandlerSpec extends Specification {


    def "Test handle nulls"() {
        setup:

        when:
        BannerGeneralExceptionHandler handler = new BannerGeneralExceptionHandler();
        ErrorResponse response = handler.handle(new ExceptionCollector(), null)


        then:
        thrown IllegalArgumentException
    }

    def "Test handle"() {
        setup:
        RequestAttributes mockRequest = new ServletWebRequest(new MockHttpServletRequest("GET", "/test"))
        RequestContextHolder.setRequestAttributes(mockRequest)

        when:
        BannerGeneralExceptionHandler handler = new BannerGeneralExceptionHandler();
        def mock = Mock(ExceptionHandlerContext)
        ErrorResponse response = handler.handle(new RuntimeException("Error"), mock)


        then:
        response != null
    }


    def "Test support"() {
        setup:

        when:
        BannerGeneralExceptionHandler handler = new BannerGeneralExceptionHandler();
        def check = handler.supports(new RuntimeException())


        then:

        check == true
    }

}
