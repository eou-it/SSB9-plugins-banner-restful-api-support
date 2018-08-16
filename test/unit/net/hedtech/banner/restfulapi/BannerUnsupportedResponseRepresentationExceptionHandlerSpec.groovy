/* ****************************************************************************
Copyright 2013-2018 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandlerContext
import net.hedtech.restfulapi.Localizer
import net.hedtech.restfulapi.UnsupportedResponseRepresentationException
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletWebRequest
import spock.lang.Specification

class BannerUnsupportedResponseRepresentationExceptionHandlerSpec extends Specification {


    def "Test handle nulls"() {
        setup:
        RequestAttributes mockRequest = new ServletWebRequest(new MockHttpServletRequest("GET", "/test"))
        RequestContextHolder.setRequestAttributes(mockRequest)
        def mockCtx = new ExceptionHandlerContext()
        mockCtx.localizer = new Localizer( {x -> return null})

        when:
        BannerUnsupportedResponseRepresentationExceptionHandler handler = new BannerUnsupportedResponseRepresentationExceptionHandler();

        ErrorResponse response = handler.handle(new UnsupportedResponseRepresentationException("error", "test"), mockCtx)


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
        BannerUnsupportedResponseRepresentationExceptionHandler handler = new BannerUnsupportedResponseRepresentationExceptionHandler();
        ErrorResponse response = handler.handle(new UnsupportedResponseRepresentationException("error", "test"), mockCtx)

        then:
        response != null
    }


    def "Test support"() {
        setup:

        when:
        BannerUnsupportedResponseRepresentationExceptionHandler handler = new BannerUnsupportedResponseRepresentationExceptionHandler();
        def check = handler.supports(new UnsupportedResponseRepresentationException("test", "test"))


        then:

        check == true
    }

}
