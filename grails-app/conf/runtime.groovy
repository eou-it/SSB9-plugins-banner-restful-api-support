import net.hedtech.banner.restfulapi.BannerApplicationExceptionHandler

/*******************************************************************************
 Copyright 2019 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

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
