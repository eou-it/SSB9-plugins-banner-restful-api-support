package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandlerContext
import net.hedtech.restfulapi.exceptionhandlers.ApplicationExceptionHandler
import net.hedtech.restfulapi.ExceptionHandler
import org.springframework.validation.FieldError


class BannerApplicationExceptionHandler extends ApplicationExceptionHandler implements ExceptionHandler {

    @Override
    ErrorResponse handle(Throwable e, ExceptionHandlerContext context) {
        def response = super.handle(e, context)
        List<Map> newContent = []
        //Handle BusinessLogicValidationException v2
        if (response.content instanceof Map &&
                response.content.hasProperty('messageCode') &&
                response.content.hasProperty('message'))
            newContent << ['code': response.content.messageCode,
                           'message': response.content.message.encodeAsHTML()]
        //Handle various SQLExceptions or other exceptions where errors is populated.
        else if( response.content && response.content.hasProperty('errors')) {
              // Pre-localized API thrown exceptions
            if( response.content.errors instanceof List<String> ) {
                response.content.errors.each { it ->
                    newContent << ['code': "Global.Internal.Error", 'message': it.encodeAsHTML()]
                }
            } //Multi-Model validation error
            else if( response.content.errors instanceof List<FieldError> ) {
                response.content.errors.each { it ->
                    newContent << ['code': "Global.Internal.Error", 'message': it.message?.encodeAsHTML()]
                }
            } // SQL exception.getMessage()
            else if( response.content.errors instanceof String ) {
                newContent << ['code': "Global.Internal.Error", 'message': response.content.errors.encodeAsHTML()]
            } //Some unexpected object found in errors.
            else {
                newContent << ['code': "Global.Internal.Error", 'message': response.content.errors.toString().encodeAsHTML()]
            }
        } //Default to handle the rest.
        else if( !response.content && response.hasProperty('message')) {
            newContent << ['code': "Global.Internal.Error", 'message': response.message.encodeAsHTML()]
        }
        if( newContent.size() > 0 )
            response.setContent([errors:newContent])
        response
    }
}
