/* ****************************************************************************
Copyright 2013 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.api.security

import grails.converters.JSON
import net.hedtech.banner.restfulapi.ApiErrorFactory
import net.hedtech.restfulapi.MediaType
import net.hedtech.restfulapi.MediaTypeParser

import javax.servlet.http.HttpServletResponse

class AuthErrorResponse {

    HttpServletResponse response
    String type
    Map errors

    MediaTypeParser mediaTypeParser = new MediaTypeParser()

    AuthErrorResponse(HttpServletResponse response, String type, Map errors) {
        this.response = response
        MediaType[] acceptedTypes = mediaTypeParser.parse(type)
        this.type = acceptedTypes.size() > 0 ? acceptedTypes[0].name : ""
        this.errors = errors
    }

    void sendResponse() throws IOException,
            javax.servlet.ServletException {
        String contentType
        String content

        switch (this.type) {
            case ~/.*xml.*/:
                contentType = 'application/xml'
                content = "<errors><error><code>${errors.code}</code><description>${errors.message}</description></error></errors>"
                break
            case ~/.*json.*/:
                contentType = 'application/json'
                content = ApiErrorFactory.create(ApiErrorFactory.V2_ERROR_TYPE.toString(),
                        null, null, ""+errors.code, errors.message, null) as JSON
                break
            default:
                contentType = 'plain/text'
                content = errors.code + " - " + errors.message
                break
        }
        response.addHeader("Content-Type", contentType)
        PrintWriter writer = response.getWriter()
        writer.println(content)
    }
}
