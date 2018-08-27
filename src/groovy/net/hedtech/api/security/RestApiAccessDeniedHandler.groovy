/* ****************************************************************************
Copyright 2013 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.api.security

import org.springframework.http.HttpHeaders
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class RestApiAccessDeniedHandler implements AccessDeniedHandler {
 
    public void handle(HttpServletRequest request, HttpServletResponse response,
                AccessDeniedException e) throws IOException,
                javax.servlet.ServletException 
    {
        if (response.isCommitted()) {
            return
        }
        def errors = [code: HttpServletResponse.SC_FORBIDDEN, message: e.getMessage()?: "Access forbidden"]
        response.setStatus(HttpServletResponse.SC_FORBIDDEN)
        AuthErrorResponse generator = new AuthErrorResponse( response,
                                           request.getHeader(HttpHeaders.ACCEPT), 
                                           errors )
        generator.sendResponse()
        return
    }
}
