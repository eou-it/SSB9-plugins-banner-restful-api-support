/* ******************************************************************************
 Copyright 2014-2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandler
import net.hedtech.restfulapi.ExceptionHandlerContext
import net.hedtech.restfulapi.Methods

public class BannerUnsupportedMethodExceptionHandler implements ExceptionHandler {

    /**
     * Will catch UnsupportedMethodException unless explicitly catch by higher priority handlers
     * @param t
     * @return
     */
    @Override
    boolean supports(Throwable t) {
       return  (t instanceof net.hedtech.restfulapi.UnsupportedMethodException)
    }

    @Override
    ErrorResponse handle(Throwable t, ExceptionHandlerContext context) {
        def response = new ErrorResponse()
        response.httpStatusCode = 405

        def allowedHTTPMethods = []
        t.getSupportedMethods().each {
            allowedHTTPMethods.add(Methods.getHttpMethod(it))
        }
        String msg =context.localizer.message( code: 'default.rest.method.not.allowed.message' )

        response.content=(ApiErrorFactory.create(ApiErrorFactory.V2_ERROR_TYPE,
                null,
                null,
                "Operation.Not.Permitted",
                msg,"Operation not permitted"))

        response.headers['Allow']=allowedHTTPMethods
        response.headers[ApiErrorFactory.HEADER_RESPONSE_TYPE]=ApiErrorFactory.V2_ERROR_TYPE
        return response
    }

}
