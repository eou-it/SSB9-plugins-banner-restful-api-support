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

class AuthErrorResponse {

    HttpServletResponse response
    String type
    String message

    MediaTypeParser  mediaTypeParser = new MediaTypeParser()

    AuthErrorResponse (HttpServletResponse response, String type, String message) {
        this.message = message
        this.response = response
        MediaType[] acceptedTypes = mediaTypeParser.parse(type)
        this.type = acceptedTypes.size() > 0 ? acceptedTypes[0].name : ""
    }
    
    void sendResponse() throws IOException,
                javax.servlet.ServletException
    {
        String contentType
        String content

        switch(this.type) {
            case ~/.*xml.*/:
                contentType = 'application/xml'
                content = "<Errors><Error><Code>${response.getStatus()}</Code>><Message>${this.message}</Message></Error></Errors>"
                break
            case ~/.*json.*/:
                contentType = 'application/json'
                content = "{ \"errors\" : [ { \"code\":\"${response.getStatus()}\" , \"message\":\"${this.message}\" } ] }"
                break
            default:
                contentType = 'plain/text'
                content = response.getStatus() + " - " + this.message
                break
        }
        response.addHeader("Content-Type", contentType )
        PrintWriter writer = response.getWriter()
        writer.println(content)
    }
}
