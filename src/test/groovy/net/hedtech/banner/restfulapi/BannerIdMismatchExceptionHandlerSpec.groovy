/* ****************************************************************************
Copyright 2013-2018 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandlerContext
import net.hedtech.restfulapi.IdMismatchException
import net.hedtech.restfulapi.Localizer
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletWebRequest
import spock.lang.Specification

class BannerIdMismatchExceptionHandlerSpec extends Specification {


    def "Test handle nulls"() {
        setup:
        RequestAttributes mockRequest = new ServletWebRequest(new MockHttpServletRequest("GET", "/test"))
        RequestContextHolder.setRequestAttributes(mockRequest)
        def mockCtx = new ExceptionHandlerContext()
        mockCtx.localizer = new Localizer( {x -> return null})

        when:
        BannerIdMismatchExceptionHandler handler = new BannerIdMismatchExceptionHandler();

        ErrorResponse response = handler.handle(new IdMismatchException("error"), mockCtx)


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
        BannerIdMismatchExceptionHandler handler = new BannerIdMismatchExceptionHandler();
        ErrorResponse response = handler.handle(new IdMismatchException("error"), mockCtx)

        then:
        response != null
    }


    def "Test support"() {
        setup:

        when:
        BannerIdMismatchExceptionHandler handler = new BannerIdMismatchExceptionHandler();
        def check = handler.supports(new IdMismatchException("test"))


        then:

        check == true
    }

}
