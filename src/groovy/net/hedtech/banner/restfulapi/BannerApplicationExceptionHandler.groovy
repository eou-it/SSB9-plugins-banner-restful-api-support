package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandlerContext
import net.hedtech.restfulapi.exceptionhandlers.ApplicationExceptionHandler
import net.hedtech.restfulapi.ExceptionHandler

class BannerApplicationExceptionHandler extends ApplicationExceptionHandler implements ExceptionHandler {

    @Override
    ErrorResponse handle(Throwable e, ExceptionHandlerContext context) {
        def response = super.handle(e, context)
        List<Map> newContent = []

        if( response.content instanceof Map && response.content.errors ){
            //Handle BusinessLogicValidationException v2
            if( response.content.errors instanceof Map &&
                    response.content.errors.hasProperty('messageCode') &&
                    response.content.errors.hasProperty('message'))
                newContent << ['code': response.content.messageCode,
                           'message': response.content.message.encodeAsHTML()]
            else if( response.content.errors instanceof List ) {
                response.content.errors.each { it ->
                    if (it instanceof Map)
                        newContent << ['code': "Global.Internal.Error", 'message': it.message?.encodeAsHTML()]
                    if (it instanceof String)
                        newContent << ['code': "Global.Internal.Error", 'message': it.encodeAsHTML()]
                }
            }
            else if( response.content.errors instanceof String ) {
                newContent << ['code': "Global.Internal.Error", 'message': response.content.errors.encodeAsHTML()]
            } //Some unexpected object found in errors.
            else {
                newContent << ['code': "Global.Internal.Error", 'message': response.content.errors.toString().encodeAsHTML()]
            }
        } //Default to handle the rest.
        else if( response.hasProperty('message')) {
            newContent << ['code': "Global.Internal.Error", 'message': response.message.encodeAsHTML()]
        }
        if( newContent.size() > 0 )
            response.setContent([errors:newContent])
        response
    }
}
