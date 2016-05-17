/* ******************************************************************************
 Copyright 2014-2015 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.restfulapi

import grails.util.Holders
import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandlerContext
import net.hedtech.restfulapi.exceptionhandlers.ApplicationExceptionHandler
import net.hedtech.restfulapi.ExceptionHandler

class BannerApplicationExceptionHandler extends ApplicationExceptionHandler implements ExceptionHandler {
    static String SCHEMA_ERROR = "Global.SchemaValidation.Error"
    static String SCHEMA_MESSAGE = "Errors parsing input JSON."
    static String INTERNAL_ERROR = "Global.Internal.Error"

    @Override
    ErrorResponse handle(Throwable e, ExceptionHandlerContext context) {
        def response = super.handle(e, context)
        List<Map> newContent = []

        if( response.content instanceof Map && response.content.errors ){
            //Handle BusinessLogicValidationException
            if( response.content.errors instanceof List ) {
                response.content.errors.each { error ->
                    if( error instanceof Map ) {
                        if (error.containsKey('messageCode') &&
                                error.containsKey('message')) {
                            if (Holders.config.restfulapi.apiErrorCodes.contains(error.messageCode)) {
                                newContent << ['code'   : error.messageCode,
                                               'message': error.message.encodeAsHTML(),
                                               'description':error.message.encodeAsHTML()]
                            } else {
                                newContent << ['code'   : SCHEMA_ERROR,
                                               'message': error.message.encodeAsHTML(),
                                               'description': SCHEMA_MESSAGE]
                            }
                        }else {
                            newContent << ['code': SCHEMA_ERROR, 'message': error.message?.encodeAsHTML(), 'description': SCHEMA_MESSAGE]
                        }
                    } else if (error instanceof String)
                        newContent << ['code': SCHEMA_ERROR, 'message': error.encodeAsHTML(), 'description': SCHEMA_MESSAGE]
                }
            }
            else if( response.content.errors instanceof String ) {
                newContent << ['code': SCHEMA_ERROR, 'message': response.content.errors.encodeAsHTML(), 'description': SCHEMA_MESSAGE]
            } //Some unexpected object found in errors.
            else {
                newContent << ['code': SCHEMA_ERROR, 'message': response.content.errors.toString().encodeAsHTML(), 'description': SCHEMA_MESSAGE]
            }
        } //Default to handle the rest.
        else if( response.hasProperty('message')) {
            if( response.httpStatusCode == 500 ) {
                newContent << ['code': INTERNAL_ERROR, 'message': response.message.encodeAsHTML(), 'description': 'Unspecified Error on the system which prevented execution.']
            }
            else {
                newContent << ['code': SCHEMA_ERROR, 'message': response.message.encodeAsHTML(), 'description': SCHEMA_MESSAGE]
            }
        }
        if( newContent.size() > 0 )
            response.setContent([errors:newContent])
        response
    }
}
