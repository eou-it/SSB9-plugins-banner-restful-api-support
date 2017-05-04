/*******************************************************************************
 Copyright 2013-2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

import grails.plugin.springsecurity.SecurityConfigType
import net.hedtech.banner.configuration.ApplicationConfigurationUtils as ConfigFinder
import net.hedtech.banner.restfulapi.BannerApplicationExceptionHandler

println "appName -> ${appName}"

grails.config.locations = [] // leave this initialized to an empty list, and add your locations in the map below.

def locationAdder = ConfigFinder.&addLocation.curry(grails.config.locations)

[BANNER_APP_CONFIG         : "banner_configuration.groovy",
 BANNER_CORE_TESTAPP_CONFIG: "banner_core_testapp_configuration.groovy"
].each { envName, defaultFileName -> locationAdder(envName, defaultFileName) }

grails.config.locations.each {
    println "config location -> " + it
}

formControllerMap = [
        'foo'       : ['API_TEST_FOO_SERVICE_API'],
        'restfulapi': ['API_RESTFULAPI']
]

grails {
    plugin {
        springsecurity {
            logout {
                afterLogoutUrl = "/"
                mepErrorLogoutUrl = '/logout/logoutPage'
            }
            useRequestMapDomainClass = false
            securityConfigType = SecurityConfigType.InterceptUrlMap
            interceptUrlMap = [
                    '/'         : ['IS_AUTHENTICATED_ANONYMOUSLY'],
                    '/login/**' : ['IS_AUTHENTICATED_ANONYMOUSLY'],
                    '/logout/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
                    '/index'    : ['IS_AUTHENTICATED_ANONYMOUSLY'],
                    '/api/resources' : ['IS_AUTHENTICATED_FULLY'],
                    '/**'       : ['ROLE_DETERMINED_DYNAMICALLY']
            ]
        }
    }
}

useRestApiAuthenticationEntryPoint = true
apiOracleUsersProxied = false
avoidSessionsFor = ['api', 'qapi']
apiUrlPrefixes = ['qapi', 'api', 'rest', 'ui']
ssLoginWorkflowIgnoreUri = ['/api/', '/qapi/']

restfulApiConfig = {

    // Overriding default exception handlers to provide errors in content body.
    exceptionHandlers {
        handler {
            instance = new BannerApplicationExceptionHandler()
            priority = 1
        }
    }

    resource 'foo' config {
        serviceName = 'fooService'
        representation {
            mediaTypes = ["application/vnd.hedtech.v1+json", "application/json"]
            marshallers {
            }
            jsonExtractor {}
        }
    }

    resource 'resources' config {
        serviceName = 'supportedResourceService'
        methods = ['list']
        representation {
            mediaTypes = ["application/vnd.hedtech.v2+json", "application/vnd.hedtech.v1+json", "application/json", "application/xml"]
            unsupportedMediaTypeMethods = ['application/vnd.hedtech.v1+json': ['list']]
            marshallers {
                jsonBeanMarshaller {
                    supports net.hedtech.integration.resource.SupportedResource
                    supports net.hedtech.integration.resource.SupportedRepresentation
                    field 'mediaType' name 'X-Media-Type'
                }
            }
        }

    }

}

restfulapi.apiErrorCodes = [
        "Global.Internal.Error",
        "Global.SchemaValidation.Error",
        "Global.UnauthorizedOperation"
]
