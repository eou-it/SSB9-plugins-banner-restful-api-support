/*******************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.restfulapi

import grails.util.Holders
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.BusinessLogicValidationException
import net.hedtech.banner.testing.BaseIntegrationTestCase
import net.hedtech.banner.testing.TermController
import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandlerContext
import net.hedtech.restfulapi.Localizer
import org.codehaus.groovy.grails.plugins.testing.GrailsMockHttpServletRequest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

import javax.servlet.http.HttpServletRequest

/**
 * Test class for BannerApplicationExceptionHandler
 */
class BannerApplicationExceptionHandlerIntegrationTests extends BaseIntegrationTestCase {

    ApplicationException applicationException
    ExceptionHandlerContext exceptionHandlerContext
    BannerApplicationExceptionHandler bannerApplicationExceptionHandler


    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()

        List errorProperties = ["#/instructorRoster.instructor.id", "#/term.code"]
        BusinessLogicValidationException businessLogicValidationException = new BusinessLogicValidationException("blank.message", ["S9034823", "default.home.label"], errorProperties)
        applicationException = new ApplicationException(this.getClass(), businessLogicValidationException)
        exceptionHandlerContext = new ExceptionHandlerContext(pluralizedResourceName: "foo", localizer: new Localizer(new TermController().localizer))
        bannerApplicationExceptionHandler = new BannerApplicationExceptionHandler()
    }


    @After
    public void tearDown() {
        super.tearDown()
    }


    @Test
    void testHandle_BusinessLogicValidationException_MessageCodeNotInApiErrorCodes() {
        setAcceptHeader("application/vnd.hedtech.integration.v1+json")

        assertFalse Holders.config.restfulapi.apiErrorCodes.contains(applicationException.wrappedException.messageCode)

        ErrorResponse errorResponse = bannerApplicationExceptionHandler.handle(applicationException, exceptionHandlerContext)
        assertNotNull errorResponse
        assertEquals 400, errorResponse.httpStatusCode
        assertEquals BannerApplicationExceptionHandler.SCHEMA_ERROR, errorResponse.content.errors[0].code
    }


    @Test
    void testHandle_BusinessLogicValidationException_MessageCodeInApiErrorCodes() {
        setAcceptHeader("application/vnd.hedtech.integration.v4+json")

        Holders.config.restfulapi.apiErrorCodes << applicationException.wrappedException.messageCode
        assertTrue Holders.config.restfulapi.apiErrorCodes.contains(applicationException.wrappedException.messageCode)

        ErrorResponse errorResponse = bannerApplicationExceptionHandler.handle(applicationException, exceptionHandlerContext)
        assertNotNull errorResponse
        assertEquals 400, errorResponse.httpStatusCode
        assertEquals applicationException.wrappedException.messageCode, errorResponse.content.errors[0].code

        Holders.config.restfulapi.apiErrorCodes.remove(applicationException.wrappedException.messageCode)
    }


    private void setAcceptHeader(String acceptHeader) {
        GrailsMockHttpServletRequest request = getHttpServletRequest()
        request.addHeader("Accept", acceptHeader)
    }


    private HttpServletRequest getHttpServletRequest() {
        HttpServletRequest request
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes()
        if (requestAttributes && requestAttributes instanceof ServletRequestAttributes) {
            request = ((ServletRequestAttributes) requestAttributes).getRequest()
        }
        return request
    }

}
