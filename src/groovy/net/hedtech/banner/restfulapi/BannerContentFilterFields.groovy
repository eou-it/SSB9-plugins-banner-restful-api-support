/* ****************************************************************************
Copyright 2016 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.ContentFilterFields

/**
 * A content filter fields implementation for use with the 'restful-api' plugin.
 **/
class BannerContentFilterFields extends BannerFilterConfig
                                implements ContentFilterFields {


    /**
     * Retrieve list of field patterns to be filtered from content.
     **/
    public List retrieveFieldPatterns(String resourceName) {
        def fieldPatterns = []
        retrieveFilterConfig(resourceName).each { config ->
            if (config.value.configEnabled) {
                fieldPatterns.add(config.key)
            }
        }
        fieldPatterns = fieldPatterns.unique().sort()
        log.debug("Field patterns=$fieldPatterns")
        return fieldPatterns
    }
}
