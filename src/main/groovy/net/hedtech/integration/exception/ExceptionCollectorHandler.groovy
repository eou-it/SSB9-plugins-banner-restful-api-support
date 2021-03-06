/* ****************************************************************************
Copyright 2013-2020 Ellucian Company L.P. and its affiliates.
******************************************************************************/

package net.hedtech.integration.exception

import net.hedtech.banner.restfulapi.ApiErrorFactory
import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandler
import net.hedtech.restfulapi.ExceptionHandlerContext
import org.apache.commons.logging.LogFactory
import org.springframework.util.Assert

/**
 * Converts RowInfoActionableException into a result object
 */

import groovy.util.logging.Slf4j
@ Slf4j
class ExceptionCollectorHandler implements ExceptionHandler {

//    private static log = LogFactory.getLog(ExceptionCollectorHandler.class)

    @Override
    boolean supports(Throwable t) {
        return t instanceof ExceptionCollector
    }

    @Override
    ErrorResponse handle(Throwable t, ExceptionHandlerContext context) {
        def response = new ErrorResponse()
        response.httpStatusCode = 500
        Assert.notNull(context )

        def localizer = context?.localizer
        ExceptionCollector collector = (ExceptionCollector) t
        response.content = ["errors":[]]

        log.debug("Exception has " + collector.exceptionList.size() + " errors")

        collector.exceptionList.each { rowInfo ->

            def msg = localizer.message( code: "default.dataretrieval.error", args: [rowInfo.resourceName, rowInfo.actionableId,rowInfo
                    .wrappedException?.localizedMessage] )

            response.content["errors"].addAll( ApiErrorFactory.create( ApiErrorFactory.V2_ERROR_TYPE,
                                    rowInfo.guid,
                                  rowInfo.actionableId,
                                  "General.retrievingData",
                                   msg,"General error retrieving data")["errors"]  )
        }

        response.headers[ApiErrorFactory.HEADER_RESPONSE_TYPE]=ApiErrorFactory.V2_ERROR_TYPE
        return response
    }
}
