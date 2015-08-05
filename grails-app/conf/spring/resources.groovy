/*******************************************************************************
 Copyright 2013-2015 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
import net.hedtech.api.security.RestApiAccessDeniedHandler
import net.hedtech.api.security.RestApiAuthenticationEntryPoint
import net.hedtech.banner.restfulapi.RestfulApiServiceBaseAdapter

/**
 * Spring bean configuration using Groovy DSL, versus normal Spring XML.
 */
beans = {

    restfulServiceAdapter(RestfulApiServiceBaseAdapter)

    restApiAuthenticationEntryPoint(RestApiAuthenticationEntryPoint) {
        realmName = 'Banner REST API Realm'
    }

    restApiAccessDeniedHandler(RestApiAccessDeniedHandler)

}

