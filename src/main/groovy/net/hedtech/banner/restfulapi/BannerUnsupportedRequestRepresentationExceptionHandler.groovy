/* ******************************************************************************
 Copyright 2014-2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandler
import net.hedtech.restfulapi.ExceptionHandlerContext

public class BannerUnsupportedRequestRepresentationExceptionHandler implements ExceptionHandler {

    /**
     * Will catch UnsupportedRequestRepresentationException unless explicitly catch by higher priority handlers
     * @param t
     * @return
     */
    @Override
    boolean supports(Throwable t) {
       return  (t instanceof net.hedtech.restfulapi.UnsupportedRequestRepresentationException)
    }

    @Override
    ErrorResponse handle(Throwable t, ExceptionHandlerContext context) {
        def response = new ErrorResponse()
        response.httpStatusCode = 415

        String msg =context.localizer.message(
                code: "default.rest.unknownrepresentation.message",
                args: [ t.getPluralizedResourceName(), t.getContentType() ])

        response.content=(ApiErrorFactory.create(ApiErrorFactory.V2_ERROR_TYPE,
                null,
                null,
                "unknownrepresentation.error",
                msg,"Unknown representation error"))


        response.headers[ApiErrorFactory.HEADER_RESPONSE_TYPE]=ApiErrorFactory.V2_ERROR_TYPE
        return response
    }

}
