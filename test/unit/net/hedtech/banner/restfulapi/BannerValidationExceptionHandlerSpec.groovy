/* ****************************************************************************
Copyright 2013-2018 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.banner.restfulapi

import grails.validation.ValidationException
import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandlerContext
import net.hedtech.restfulapi.Localizer
import org.codehaus.groovy.grails.plugins.testing.GrailsMockErrors
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletWebRequest
import spock.lang.Specification



class BannerValidationExceptionHandlerSpec extends Specification {


    def "Test handle nulls"() {
        setup:
        RequestAttributes mockRequest = new ServletWebRequest(new MockHttpServletRequest("GET", "/test"))
        RequestContextHolder.setRequestAttributes(mockRequest)
        def mockCtx = new ExceptionHandlerContext()
        mockCtx.localizer = new Localizer( {x -> return null})

        when:
        BannerValidationExceptionHandler handler = new BannerValidationExceptionHandler();

        ErrorResponse response = handler.handle(new ValidationException("Fields is not valid", new GrailsMockErrors()), mockCtx)


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
        BannerValidationExceptionHandler handler = new BannerValidationExceptionHandler();
        ErrorResponse response = handler.handle(new ValidationException("Fields is not valid", new GrailsMockErrors()), mockCtx)

        then:
        response != null
    }


    def "Test support"() {
        setup:

        when:
        BannerValidationExceptionHandler handler = new BannerValidationExceptionHandler();
        def check = handler.supports(new ValidationException("not valid", new GrailsMockErrors()))


        then:

        check == true
    }

}
