/* ******************************************************************************
 Copyright 2013-2015 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

import grails.util.Holders
import org.springframework.security.web.access.ExceptionTranslationFilter
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

class BannerRestfulApiSupportGrailsPlugin {
    String version = "2.5.1"

    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.2.1 > *"

    //must load after banner-core to override basic auth
    //and exception filters if configured to do so.
    def loadAfter = ['banner-core']

    // resources that are excluded from plugin packaging
    def pluginExcludes = ["grails-app/views/error.gsp"]

    def author = "ellucian"
    def authorEmail = ""
    def title = "Banner Restful Api Support Plugin"
    def description = '''This plugin adds Banner specific service adapters
                         |and endpoint security classes to support the use of
                         |the restful-api plugin in Banner applications.'''.stripMargin()

    def documentation = ""

    def doWithSpring = {
        if (Holders.config.useRestApiAuthenticationEntryPoint) {
            basicAuthenticationFilter(BasicAuthenticationFilter) {
                authenticationManager = ref('authenticationManager')
                authenticationEntryPoint = ref('restApiAuthenticationEntryPoint')
            }

            basicExceptionTranslationFilter(ExceptionTranslationFilter) {
                authenticationEntryPoint = ref('restApiAuthenticationEntryPoint')
                accessDeniedHandler = ref('restApiAccessDeniedHandler')
            }
        }
    }
}
