#banner-restful-api-support

This plugin contains support classes for banner xe applications to use the restful-api plugin, including classes to secure endpoints in a banner xe specific fashion and a service adapter.

The contents of this application are tested within the banner_core_testapp application.

##Using the service adapter.
This plugin contains a restful-api service adapter for use with Banner XE services.  To use it, declare it as a bean named restfulServiceAdapter in the resources.groovy file.  For example:

    import net.hedtech.banner.restfulapi.RestfulApiServiceBaseAdapter

    beans = {
        restfulServiceAdapter( RestfulApiServiceBaseAdapter )
    }

##Enabling Basic Authentication Entry Point
This plugin contains an implementation of the Basic Authentication Entry Point that correctly returns a json or xml response to authentication failures (instead of html.)

To enable it, add

    useRestApiAuthenticationEntryPoint = true

to Config.groovy, and define the bean in your resources.groovy, e.g.:

    restApiAuthenticationEntryPoint(RestApiAuthenticationEntryPoint) {
        realmName = 'Banner REST API Realm'
    }
