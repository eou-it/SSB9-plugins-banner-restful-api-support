/* ****************************************************************************
Copyright 2016 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.contentfilters.BasicContentFilter

/**
 * A content filter implementation for use with the 'restful-api' plugin.
 **/
class BannerContentFilter extends BasicContentFilter {

    // Content filter configuration
    //  - set allowPartialRequest=true to allow partial request content
    //  - set bypassCreateRequest=true to bypass filtering of create request content
    boolean allowPartialRequest = true
    boolean bypassCreateRequest = true

}
