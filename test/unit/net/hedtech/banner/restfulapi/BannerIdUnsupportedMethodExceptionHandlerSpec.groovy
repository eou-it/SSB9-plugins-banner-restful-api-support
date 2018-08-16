/* ****************************************************************************
Copyright 2013-2018 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandlerContext
import net.hedtech.restfulapi.Localizer
import net.hedtech.restfulapi.UnsupportedMethodException
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletWebRequest
import spock.lang.Specification

class BannerIdUnsupportedMethodExceptionHandlerSpec extends Specification {


    def "Test handle nulls"() {
        setup:
        RequestAttributes mockRequest = new ServletWebRequest(new MockHttpServletRequest("GET", "/test"))
        RequestContextHolder.setRequestAttributes(mockRequest)
        def mockCtx = new ExceptionHandlerContext()
        mockCtx.localizer = new Localizer( {x -> return null})

        when:
        BannerUnsupportedMethodExceptionHandler handler = new BannerUnsupportedMethodExceptionHandler();

        ErrorResponse response = handler.handle(new UnsupportedMethodException(), mockCtx)


        then:
        thrown IllegalArgumentException
    }

    def "Test handle"() {
        setup:
        RequestAttributes mockRequest = new ServletWebRequest(new MockHttpServletRequest("GET", "/test"))
        RequestContextHolder.setRequestAttributes(mockRequest)
        def mockCtx = new ExceptionHandlerContext()
        mockCtx.localizer = new Localizer( {x -> return "test"})

        when:
        BannerUnsupportedMethodExceptionHandler handler = new BannerUnsupportedMethodExceptionHandler();
        ErrorResponse response = handler.handle(new UnsupportedMethodException(), mockCtx)

        then:
        response != null
    }


    def "Test support"() {
        setup:

        when:
        BannerUnsupportedMethodExceptionHandler handler = new BannerUnsupportedMethodExceptionHandler();
        def check = handler.supports(new UnsupportedMethodException())


        then:

        check == true
    }

}
