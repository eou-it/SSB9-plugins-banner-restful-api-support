/*******************************************************************************
 Copyright 2013-2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
import net.hedtech.api.security.RestApiAccessDeniedHandler
import net.hedtech.api.security.RestApiAuthenticationEntryPoint
import net.hedtech.banner.restfulapi.RestfulApiServiceBaseAdapter
import net.hedtech.restfulapi.ResourceDetailList

/**
 * Spring bean configuration using Groovy DSL, versus normal Spring XML.
 */
beans = {

    restfulServiceAdapter(RestfulApiServiceBaseAdapter)

    restApiAuthenticationEntryPoint(RestApiAuthenticationEntryPoint) {
        realmName = 'Banner REST API Realm'
    }

    restApiAccessDeniedHandler(RestApiAccessDeniedHandler)

    // Resource detail list (for reporting and discovery) - initialized by restfulApiController
    resourceDetailList(ResourceDetailList)

}

