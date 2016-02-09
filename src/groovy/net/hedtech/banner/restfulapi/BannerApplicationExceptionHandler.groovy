/* ******************************************************************************
 Copyright 2014-2016 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.restfulapi

import grails.util.Holders
import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandler
import net.hedtech.restfulapi.ExceptionHandlerContext
import net.hedtech.restfulapi.exceptionhandlers.ApplicationExceptionHandler
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

import javax.servlet.http.HttpServletRequest

class BannerApplicationExceptionHandler extends ApplicationExceptionHandler implements ExceptionHandler {

    static String SCHEMA_ERROR = "Global.SchemaValidation.Error"
    static String SCHEMA_MESSAGE = "Errors parsing input JSON."
    static String INTERNAL_ERROR = "Global.Internal.Error"


    @Override
    ErrorResponse handle(Throwable e, ExceptionHandlerContext context) {
        def response = super.handle(e, context)

        List<Map> newContent = []
        if (response.content instanceof Map && response.content.errors) {
            //Handle BusinessLogicValidationException
            if (response.content.errors instanceof List) {
                response.content.errors.each { error ->
                    if (error instanceof Map) {
                        if (error.containsKey('messageCode') &&
                                error.containsKey('message')) {
                            if (needHEDMResponse() && getAcceptVersion() > 4) {
                                // Send response as per HEDM Error Message Schema
                                newContent << getHEDMErrorMessage(error)
                            } else {
                                if (Holders.config.restfulapi.apiErrorCodes.contains(error.messageCode)) {
                                    newContent << ['code'       : error.messageCode,
                                                   'message'    : error.message.encodeAsHTML(),
                                                   'description': error.message.encodeAsHTML()]
                                } else {
                                    newContent << ['code'       : SCHEMA_ERROR,
                                                   'message'    : error.message.encodeAsHTML(),
                                                   'description': SCHEMA_MESSAGE]
                                }
                            }
                        } else {
                            newContent << ['code': SCHEMA_ERROR, 'message': error.message?.encodeAsHTML(), 'description': SCHEMA_MESSAGE]
                        }
                    } else if (error instanceof String)
                        newContent << ['code': SCHEMA_ERROR, 'message': error.encodeAsHTML(), 'description': SCHEMA_MESSAGE]
                }
            } else if (response.content.errors instanceof String) {
                newContent << ['code': SCHEMA_ERROR, 'message': response.content.errors.encodeAsHTML(), 'description': SCHEMA_MESSAGE]
            } else {
                //Some unexpected object found in errors.
                newContent << ['code': SCHEMA_ERROR, 'message': response.content.errors.toString().encodeAsHTML(), 'description': SCHEMA_MESSAGE]
            }
        } else if (response.hasProperty('message')) {
            //Default to handle the rest.
            if (response.httpStatusCode == 500) {
                newContent << ['code': INTERNAL_ERROR, 'message': response.message.encodeAsHTML(), 'description': 'Unspecified Error on the system which prevented execution.']
            } else {
                newContent << ['code': SCHEMA_ERROR, 'message': response.message.encodeAsHTML(), 'description': SCHEMA_MESSAGE]
            }
        }
        if (newContent.size() > 0) {
            if (needHEDMResponse() && getAcceptVersion() > 4) {
                if (newContent.size() == 1) {
                    response.setContent(newContent[0])
                } else {
                    response.setContent(newContent)
                }
            } else {
                response.setContent([errors: newContent])
            }
        }

        response
    }


    static HttpServletRequest getHttpServletRequest() {
        HttpServletRequest request
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes()
        if (requestAttributes && requestAttributes instanceof ServletRequestAttributes) {
            request = ((ServletRequestAttributes) requestAttributes).getRequest()
        }
        return request
    }


    private static String responseBodyMediaType() {
        HttpServletRequest request = getHttpServletRequest()
        return request?.getHeader("Accept")
    }


    private static boolean needHEDMResponse() {
        boolean hedm = false
        String acceptHeader = responseBodyMediaType()
        if (acceptHeader) {
            hedm = acceptHeader.contains("integration")
        }
        return hedm
    }

    /**
     * RESTful APIs use custom media types (previously known as 'MIME types') for versioning.
     * HEDM APIs will have Accept headers like application/vnd.hedtech.integration.v1+json, application/vnd.hedtech.integration.v2+json so on.
     * Non-HEDM APIs will have Accept headers like application/vnd.hedtech.v1+json, application/vnd.hedtech.v2+json so on.
     *
     * Accept header can contain generic media types like application/vnd.hedtech.integration+json or application/json that
     * represent latest (current) version of the API.  In such cases, this method does not return anything.
     *
     * @return version (v1,v2 so on) extracted from Accept header
     */
    private static String getResponseRepresentationVersion() {
        String version
        String acceptHeader = responseBodyMediaType()
        if (acceptHeader) {
            int indexOfDotBeforeVersion = acceptHeader.lastIndexOf(".")
            int indexOfPlus = acceptHeader.lastIndexOf("+")
            if (indexOfDotBeforeVersion != -1 && indexOfPlus != -1 && indexOfDotBeforeVersion + 1 < indexOfPlus) {
                version = acceptHeader.substring(indexOfDotBeforeVersion + 1, indexOfPlus)
                if (!version?.toLowerCase()?.startsWith("v")) {
                    // May be generic Accept header like "application/vnd.hedtech.integration+json"
                    version = null
                }
            }
        }
        return version?.toLowerCase()
    }


    private static Integer getAcceptVersion() {
        Integer num = 0
        String version = getResponseRepresentationVersion()
        if (version) {
            num = version.substring(1).toInteger()
        }
        return num
    }


    private def getHEDMErrorMessage(def error) {
        def map = [:]
        if (Holders.config.restfulapi.apiErrorCodes.contains(error.messageCode)) {
            map.put("code", error.messageCode)
        } else {
            map.put("code", SCHEMA_ERROR)
        }
        map.put("description", error.message.encodeAsHTML())
        if (error.containsKey('errorProperties') && error.errorProperties) {
            map.put("errorProperties", error.errorProperties)
        }
        return map
    }

}
