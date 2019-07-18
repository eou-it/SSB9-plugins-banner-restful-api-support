/* ******************************************************************************
 Copyright 2014-2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandler
import net.hedtech.restfulapi.ExceptionHandlerContext

public class BannerGeneralExceptionHandler implements ExceptionHandler {

    /**
     * Will catch almost all exceptions unless explicitly catch by higher priority handlers
     * @param t
     * @return
     */
    @Override
    boolean supports(Throwable t) {
        return true
    }

    @Override
    ErrorResponse handle(Throwable t, ExceptionHandlerContext context) {
        def response = new ErrorResponse()
        response.httpStatusCode = 500

        String msg =t.localizedMessage?:t.message?:t.getClass().getName()

        response.content=(ApiErrorFactory.create(ApiErrorFactory.V2_ERROR_TYPE,
                null,
                null,
                "General.error",
                msg,"Application error"))


        response.headers[ApiErrorFactory.HEADER_RESPONSE_TYPE]=ApiErrorFactory.V2_ERROR_TYPE
        return response
    }

}
