/* ****************************************************************************
Copyright 2013 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.api.security

import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import net.hedtech.restfulapi.MediaType
import net.hedtech.restfulapi.MediaTypeParser

import org.springframework.http.HttpHeaders
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler

class RestApiAccessDeniedHandler implements AccessDeniedHandler {
 
    public void handle(HttpServletRequest request, HttpServletResponse response,
                AccessDeniedException e) throws IOException,
                javax.servlet.ServletException 
    {
        if (response.isCommitted()) {
            return
        }
        def errors = [code: HttpServletResponse.SC_FORBIDDEN, message: e.getMessage()]
        response.setStatus(HttpServletResponse.SC_FORBIDDEN)
        AuthErrorResponse generator = new AuthErrorResponse( response,
                                           request.getHeader(HttpHeaders.ACCEPT), 
                                           errors )
        generator.sendResponse()
        return
    }
}
