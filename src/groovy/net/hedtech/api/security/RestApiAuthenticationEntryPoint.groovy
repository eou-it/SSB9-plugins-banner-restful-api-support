/* ******************************************************************************
 Copyright 2013 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.api.security

import java.io.IOException
import java.io.PrintWriter

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.springframework.http.HttpHeaders
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint


public class RestApiAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    @Override
    public void commence( HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException)
                throws IOException, ServletException {

        response.addHeader("WWW-Authenticate", "Basic realm=\"" + getRealmName() + "\"")
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
        def errors = [code: HttpServletResponse.SC_UNAUTHORIZED, message: authException.getMessage()]
        AuthErrorResponse generator = new AuthErrorResponse( response, 
                                          request.getHeader(HttpHeaders.ACCEPT), 
                                          errors )
        generator.sendResponse()
        return
    }
}


