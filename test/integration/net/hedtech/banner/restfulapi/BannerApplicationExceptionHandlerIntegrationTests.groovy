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
        setAcceptHeader("application/vnd.hedtech.v1+json")

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


    @Test
    void testHandle_BusinessLogicValidationException_HEDM_aboveV4_NonHEDMMessageKey() {
        setAcceptHeader("application/vnd.hedtech.integration.v5+json")

        assertFalse applicationException.wrappedException.messageCode.startsWith("hedm.")

        ErrorResponse errorResponse = bannerApplicationExceptionHandler.handle(applicationException, exceptionHandlerContext)
        assertNotNull errorResponse
        assertEquals 400, errorResponse.httpStatusCode
        assertEquals BannerApplicationExceptionHandler.SCHEMA_ERROR, errorResponse.content.code
        assertTrue errorResponse.content.errorProperties.containsAll(applicationException.wrappedException.errorProperties)
    }


    @Test
    void testHandle_BusinessLogicValidationException_HEDM_aboveV4_HEDMMessageKey() {
        setAcceptHeader("application/vnd.hedtech.integration.v6+json")

        applicationException.wrappedException.messageCode = "hedm." + applicationException.wrappedException.messageCode
        assertTrue applicationException.wrappedException.messageCode.startsWith("hedm.")

        ErrorResponse errorResponse = bannerApplicationExceptionHandler.handle(applicationException, exceptionHandlerContext)
        assertNotNull errorResponse
        assertEquals 400, errorResponse.httpStatusCode
        assertEquals applicationException.wrappedException.messageCode.substring(5), errorResponse.content.code
        assertTrue errorResponse.content.errorProperties.containsAll(applicationException.wrappedException.errorProperties)
    }


    private void setAcceptHeader(String acceptHeader) {
        GrailsMockHttpServletRequest request = BannerApplicationExceptionHandler.getHttpServletRequest()
        request.addHeader("Accept", acceptHeader)
    }

}
