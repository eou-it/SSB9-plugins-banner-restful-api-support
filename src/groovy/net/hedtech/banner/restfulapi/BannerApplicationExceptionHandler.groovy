/* ******************************************************************************
 Copyright 2014-2015 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.restfulapi

import com.google.common.net.HttpHeaders
import grails.util.Holders
import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandler
import net.hedtech.restfulapi.ExceptionHandlerContext
import net.hedtech.restfulapi.exceptionhandlers.ApplicationExceptionHandler
import org.codehaus.groovy.grails.web.util.WebUtils

class BannerApplicationExceptionHandler extends ApplicationExceptionHandler implements ExceptionHandler {
    static String SCHEMA_ERROR = "Global.SchemaValidation.Error"
    static String SCHEMA_MESSAGE = "Errors parsing input JSON."
    static String INTERNAL_ERROR = "Global.Internal.Error"


    @Override
    ErrorResponse handle(Throwable e, ExceptionHandlerContext context) {
        def request = WebUtils.retrieveGrailsWebRequest()
        String errorVersion =  ApiErrorFactory.V2_ERROR_TYPE;


        def response = super.handle(e, context)
        def newContent = null

        if (response.content instanceof Map && response.content.errors) {
            //Handle BusinessLogicValidationException
            if (response.content.errors instanceof List) {
                response.content.errors.each { error ->
                    if (error instanceof Map) {
                        if (error.containsKey('messageCode') &&
                                error.containsKey('message')) {
                            if (Holders.config.restfulapi.apiErrorCodes.contains(
                                    error.messageCode)) {
                                newContent = ApiErrorFactory.create(errorVersion,null, null, error.messageCode,
                                        error.message.encodeAsHTML(),
                                        error.message.encodeAsHTML())
                            } else {
                                newContent = ApiErrorFactory.create(errorVersion, null,
                                        null,
                                        SCHEMA_ERROR,
                                        error.message.encodeAsHTML(),
                                        SCHEMA_MESSAGE)
                            }
                        } else {
                            newContent = ApiErrorFactory.create(errorVersion, null,
                                    null,
                                    SCHEMA_ERROR,
                                    error.message.encodeAsHTML(),
                                    SCHEMA_MESSAGE)
                        }
                    } else if (error instanceof String)
                        newContent = ApiErrorFactory.create(errorVersion, null,
                                null,
                                SCHEMA_ERROR,
                                error.encodeAsHTML(),
                                SCHEMA_MESSAGE)

                }
            } else if (response.content.errors instanceof String) {
                newContent = ApiErrorFactory.create(errorVersion, null,
                        null,
                        SCHEMA_ERROR,
                        response.content.errors.encodeAsHTML(),
                        SCHEMA_MESSAGE)

            } //Some unexpected object found in errors.
            else {
                newContent = ApiErrorFactory.create(errorVersion, null,
                        null,
                        SCHEMA_ERROR,
                        response.content.errors.toString().encodeAsHTML(),
                        SCHEMA_MESSAGE)
            }
        } //Default to handle the rest.
        else if (response.hasProperty('message')) {
            if (response.httpStatusCode == 500) {
                newContent = ApiErrorFactory.create(errorVersion, null,
                        null,
                        INTERNAL_ERROR,
                        response.message.encodeAsHTML(),
                        'Unspecified Error on the system which prevented execution.')

            } else {
                newContent = ApiErrorFactory.create(errorVersion, null,
                        null,
                        SCHEMA_ERROR,
                        response.message.encodeAsHTML(),
                        SCHEMA_MESSAGE)
            }
        }
        if (newContent.size() > 0)
            response.setContent(newContent)

        if(!errorVersion.isEmpty())
        {
            response.headers[ApiErrorFactory.HEADER_RESPONSE_TYPE]= errorVersion
        }
        response

    }


}
