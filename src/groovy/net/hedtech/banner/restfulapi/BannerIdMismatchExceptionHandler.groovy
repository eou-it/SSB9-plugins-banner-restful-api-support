/* ******************************************************************************
 Copyright 2014-2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandler
import net.hedtech.restfulapi.ExceptionHandlerContext

public class BannerIdMismatchExceptionHandler implements ExceptionHandler {

    /**
     * Will catch IdMismatchException unless explicitly catch by higher priority handlers
     * @param t
     * @return
     */
    @Override
    boolean supports(Throwable t) {
       return  (t instanceof net.hedtech.restfulapi.IdMismatchException)
    }

    @Override
    ErrorResponse handle(Throwable t, ExceptionHandlerContext context) {
        def response = new ErrorResponse()
        response.httpStatusCode = 400

        String msg =context.localizer.message(
                code: "default.rest.idmismatch.message",
                args: [ t.getPluralizedResourceName() ])

        response.content=(ApiErrorFactory.create(ApiErrorFactory.V2_ERROR_TYPE,
                null,
                null,
                "idmismatch.error",
                msg,"Id mismatch error"))


        response.headers['X-Status-Reason']='Id mismatch'
        response.headers[ApiErrorFactory.HEADER_RESPONSE_TYPE]=ApiErrorFactory.V2_ERROR_TYPE
        return response
    }

}
