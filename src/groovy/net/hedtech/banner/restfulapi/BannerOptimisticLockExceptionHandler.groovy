/* ******************************************************************************
 Copyright 2014-2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandler
import net.hedtech.restfulapi.ExceptionHandlerContext
import net.hedtech.restfulapi.Inflector

public class BannerOptimisticLockExceptionHandler implements ExceptionHandler {

    /**
     * Will catch OptimisticLockingFailureException unless explicitly catch by higher priority handlers
     * @param t
     * @return
     */
    @Override
    boolean supports(Throwable t) {
       return  (t instanceof  org.springframework.dao.OptimisticLockingFailureException)
    }

    @Override
    ErrorResponse handle(Throwable t, ExceptionHandlerContext context) {
        def response = new ErrorResponse()
        response.httpStatusCode = 409

        String msg =context.localizer.message(
                code: "default.optimistic.locking.failure",
                args: [Inflector.singularize( context.pluralizedResourceName ) ] )

        response.content=(ApiErrorFactory.create(ApiErrorFactory.V2_ERROR_TYPE,
                null,
                null,
                "default.optimistic.locking.failure",
                msg,"Optimistic locking failure"))


        response.headers[ApiErrorFactory.HEADER_RESPONSE_TYPE]=ApiErrorFactory.V2_ERROR_TYPE
        return response
    }

}
