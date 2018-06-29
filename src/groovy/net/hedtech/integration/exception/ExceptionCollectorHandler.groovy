/* ****************************************************************************
Copyright 2013-2017 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.integration.exception

import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandler
import net.hedtech.restfulapi.ExceptionHandlerContext
import org.apache.commons.logging.LogFactory

/**
 * Converts RowInfoActionableException into a result object
 */
class ExceptionCollectorHandler implements ExceptionHandler {

    private static log = LogFactory.getLog(ExceptionCollectorHandler.class)

    @Override
    boolean supports(Throwable t) {
        return t instanceof ExceptionCollector
    }

    @Override
    ErrorResponse handle(Throwable t, ExceptionHandlerContext context) {
        def response = new ErrorResponse()
        response.httpStatusCode = 500

        def localizer = context.localizer
        ExceptionCollector collector = (ExceptionCollector) t
        response.content = []

        log.debug("Exception has " + collector.exceptionList.size() + " errors")

        collector.exceptionList.each { rowInfo ->

            def msg = localizer.message( code: "default.dataretrieval.error", args: [rowInfo.resourceName, rowInfo.actionableId,rowInfo.wrappedException.localizedMessage] )

            response.content.add(["id"         : rowInfo.guid,
                                  "sourceId"   : rowInfo.actionableId,
                                  "code"       : "General.retrievingData",
                                  "title"      : "General error retrieving data",
                                  "description": msg
            ])
        }


        return response
    }
}
