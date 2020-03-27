import grails.plugin.springsecurity.SecurityConfigType
import net.hedtech.banner.restfulapi.BannerApplicationExceptionHandler

/*******************************************************************************
 Copyright 2013-2020 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

//println "appName -> ${appName}"

grails.config.locations = [
        BANNER_APP_CONFIG:           "banner_configuration.groovy"
]

grails.project.groupId = "net.hedtech" // used when deploying to a maven repo

/*def locationAdder = ConfigFinder.&addLocation.curry(grails.config.locations)*/

/*
[BANNER_APP_CONFIG         : "banner_configuration.groovy",
 BANNER_CORE_TESTAPP_CONFIG: "banner_core_testapp_configuration.groovy"
].each { envName, defaultFileName -> locationAdder(envName, defaultFileName) }
*/

/*grails.config.locations.each {
    println "config location -> " + it
}*/

grails.enable.native2ascii = false
grails.databinding.useSpringBinder=true

dataSource {
    dialect = "org.hibernate.dialect.Oracle10gDialect"
    loggingSql = false
}

hibernate {
    packagesToScan="net.hedtech.**.*"
    dialect = "org.hibernate.dialect.Oracle10gDialect"
    show_sql = false
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory'
    flush.mode = AUTO
    //naming_strategy = "org.hibernate.cfg.ImprovedNamingStrategy"
    config.location = ["classpath:hibernate-banner-core.cfg.xml",
                       "classpath:hibernate-banner-core.testing.cfg.xml",
                       "classpath:hibernate-banner-restful-api-support.cfg.xml"]
}

// environment specific settings
environments {
    development {
        dataSource {
        }
    }
    test {
        dataSource {
        }
    }
    production {
        dataSource {
        }
    }
}

formControllerMap = [
        // add NON_EXISTANT to foo resource for testing that
        // a non-authorized security object won't cause an
        // exception with the read-only API access check.
        'restfulapi'  : ['API_RESTFULAPI'],
        'foo'         : ['API_TEST_FOO_SERVICE_API','NON_EXISTANT'],
        'diagnostics' : ['API_DIAGNOSTICS']
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
                    [pattern:'/',                access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
                    [pattern:'/login/**',        access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
                    [pattern:'/logout/**',       access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
                    [pattern:'/index',           access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
                    [pattern:'/api/resources',   access: ['IS_AUTHENTICATED_FULLY']],
                    [pattern:'/**',              access: ['ROLE_DETERMINED_DYNAMICALLY']]
            ]
        }
    }
}

useRestApiAuthenticationEntryPoint = true
apiOracleUsersProxied = false
avoidSessionsFor = ['api', 'qapi']
apiUrlPrefixes = ['qapi', 'api', 'rest', 'ui']
ssLoginWorkflowIgnoreUri = ['/api/', '/qapi/']

// Force all marshallers to remove null fields and empty collections
restfulApi.marshallers.removeNullFields = true
restfulApi.marshallers.removeEmptyCollections = true

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
            mediaTypes = ["application/vnd.hedtech.integration.v1+json", "application/json"]
            marshallers {
            }
            jsonExtractor {}
        }
    }

    resource 'fooTestSupportedResources' config {
        serviceName = 'fooService'
        representation {
            mediaTypes = ["application/vnd.hedtech.integration.v2+json", "application/vnd.hedtech.integration.v1+json", "application/json", "application/xml"]
            unsupportedMediaTypeMethods = ['application/vnd.hedtech.integration.v1+json': ['create', 'update', 'delete']]
            marshallers {
            }
            jsonExtractor {}
            representationMetadata = [
                    filters: [
                            "filter1",
                            "filter2"
                    ],
                    namedQueries: [
                            query1: [
                                    filters: [
                                            "query1-filter"
                                    ]
                            ],
                            query2: [
                                    filters: [
                                            "query2-filter"
                                    ]
                            ]
                    ],
                    deprecationNotice: [
                            deprecatedOn: "2015-03-12",
                            sunsetOn: "2017-09-01",
                            description: "Resource has properties not in use"
                    ]
            ]
        }
    }

    resource 'resources' config {
        serviceName = 'supportedResourceService'
        methods = ['list']
        representation {
            mediaTypes = ["application/json"]
            marshallers {
                jsonBeanMarshaller {
                    supports net.hedtech.integration.resource.SupportedResource
                }
                jsonBeanMarshaller {
                    supports net.hedtech.integration.resource.SupportedRepresentation
                    field 'mediaType' name 'X-Media-Type'
                }
            }
        }
    }

    resource 'diagnostics' config {
        serviceName = 'resourceDiagnosticService'
        methods = ['list', 'create']
        representation {
            mediaTypes = ["application/json"]
            marshallers {
                jsonDomainMarshaller {
                    includesId false
                    includesVersion false
                    supports net.hedtech.integration.diagnostic.ResourceDiagnosticMessage
                    includesFields {
                        field 'id'
                        field 'resourceName'
                        field 'messageLevel'
                        field 'message'
                        field 'lastModified'
                    }
                }
            }
            jsonExtractor {
            }
        }

    }

}

restfulapi.apiErrorCodes = [
        "Global.Internal.Error",
        "Global.SchemaValidation.Error",
        "Global.UnauthorizedOperation"
]

// resources excluded from the /resources api response
supportedResource.excludedResources = [
        // exclude administrative resources
        "diagnostics",
        "resources"
]
