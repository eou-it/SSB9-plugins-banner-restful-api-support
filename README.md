#banner-restful-api-support

This plugin contains support classes for banner xe applications to use the restful-api plugin, including classes to secure endpoints in a banner xe specific fashion and a service adapter.

The contents of this application are tested within the banner_core_testapp application.

##Using the service adapter.
This plugin contains a restful-api service adapter for use with Banner XE services.  To use it, declare it as a bean named restfulServiceAdapter in the resources.groovy file.  For example:

    import net.hedtech.banner.restfulapi.RestfulApiServiceBaseAdapter

    beans = {
        restfulServiceAdapter( RestfulApiServiceBaseAdapter )
    }

##Request parameters with the service adapter.
The Banner ServiceBase has no provision for receiving request parameters (only the entity content).  To work around this restriction,
the service adapter places the request params in the ThreadLocal net.hedtech.banner.restfulapi.RestfulApiRequestParams.  This can be used to obtain the request parameters as needed in the service; for example, to handle nested resources.

##Enabling Basic Authentication Entry Point
This plugin contains an implementation of the Basic Authentication Entry Point that correctly returns a json or xml response to authentication failures (instead of html.)

To enable it, add

    useRestApiAuthenticationEntryPoint = true

to Config.groovy, and define the bean in your resources.groovy, e.g.:

    restApiAuthenticationEntryPoint(RestApiAuthenticationEntryPoint) {
        realmName = 'Banner REST API Realm'
    }

##Additional Configuration for REST

In addition to the configuration as documented within the 'restful-api' README.md file, there are a few additional configuration items that pertain to using the restful-api plugin within Banner XE.

RESTful API endpoints should generally be 'stateless'. The Spring Security filterchain currently configures 'api' and 'qapi' URLs such that no session is created during authentication. In addition, there are two additional configuration options that should be included in your application's Config.groovy, as shown and described below. These are needed to ensure Spring Security, banner-core, and the restful-api plugin are properly configured to work together.

```groovy
// ******************************************************************************
//                             API Prefix Configuration
// ******************************************************************************
// Specify the URL prefixes used for the API. The list should include all prefixes
// that are used to expose API endpoints, regardless of whether they are protected
// using Basic Authentication or whether they are 'stateless'. See DB Connection
// Caching Configuration below, which may configure a subset of the URL prefixes
// configured here to be 'stateless'.
//
apiUrlPrefixes = [ 'api', 'qapi', 'rest', 'ui' ]


// ******************************************************************************
//                    DB Connection Caching Configuration
// ******************************************************************************
// Note: The BannerDS will cache database connections for administrative users,
//       however for RESTful APIs we do not want this behavior (even when
//       authenticated as an 'administrative' user. RESTful APIs should be stateless.
//
// IMPORTANT:
// When exposing RESTful endpoints for use by external programmatic clients, we
// will want to exclude database caching for those URLs.
// Also, if using a prefix other than 'api' and 'qapi' you will need to ensure
// the spring security filter chain is configured to avoid creating a session.
// Endpoints that will be used via Ajax on behalf of an authenticated user can
// benefit from having 'state' (with respect to their authentication), hence this
// configuration may be a subset of the 'apiUrlPrefixes' configuration above.
//
avoidSessionsFor = [ 'api', 'qapi' ]
```

