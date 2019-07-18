/* ******************************************************************************
 Copyright 2014-2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandler
import net.hedtech.restfulapi.ExceptionHandlerContext
import net.hedtech.restfulapi.Inflector

public class BannerValidationExceptionHandler implements ExceptionHandler {

    /**
     * Will catch ValidationException unless explicitly catch by higher priority handlers
     * @param t
     * @return
     */
    @Override
    boolean supports(Throwable t) {
       return  (t instanceof  grails.validation.ValidationException)
    }

    @Override
    ErrorResponse handle(Throwable t, ExceptionHandlerContext context) {
        def response = new ErrorResponse()
        response.httpStatusCode = 400

        String msg  = context.localizer.message(
                code: "default.rest.validation.errors.message",
                args: [ Inflector.singularize(context.pluralizedResourceName)])

        response.content=(ApiErrorFactory.create(ApiErrorFactory.V2_ERROR_TYPE,
                null,
                null,
                "validation",
                msg,"Validation failure"))

        response.headers['X-Status-Reason']='Validation failed'
        response.headers[ApiErrorFactory.HEADER_RESPONSE_TYPE]=ApiErrorFactory.V2_ERROR_TYPE
        return response
    }

}
