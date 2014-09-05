/* ****************************************************************************
Copyright 2013 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.api.security

import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletResponse
import java.io.PrintWriter

import net.hedtech.restfulapi.MediaType
import net.hedtech.restfulapi.MediaTypeParser

import grails.converters.JSON
import grails.converters.XML

class AuthErrorResponse {

    HttpServletResponse response
    String type
    Map errors

    MediaTypeParser  mediaTypeParser = new MediaTypeParser()

    AuthErrorResponse (HttpServletResponse response, String type, Map errors) {
        this.response = response
        MediaType[] acceptedTypes = mediaTypeParser.parse(type)
        this.type = acceptedTypes.size() > 0 ? acceptedTypes[0].name : ""
        this.errors = errors
    }
    
    void sendResponse() throws IOException,
                javax.servlet.ServletException
    {
        String contentType
        String content

        switch(this.type) {
            case ~/.*xml.*/:
                contentType = 'application/xml'
                content = "<errors><error><code>${errors.code}</code><description>${errors.message}</description></error></errors>"
                break
            case ~/.*json.*/:
                contentType = 'application/json'
                content = "{ \"errors\" : [ { \"code\": \"${errors.code}\", \"description\": \"${errors.message}\" } ] }"
                break
            default:
                contentType = 'plain/text'
                content = errors.code + " - " + errors.message
                break
        }
        response.addHeader("Content-Type", contentType )
        PrintWriter writer = response.getWriter()
        writer.println(content)
    }
}
