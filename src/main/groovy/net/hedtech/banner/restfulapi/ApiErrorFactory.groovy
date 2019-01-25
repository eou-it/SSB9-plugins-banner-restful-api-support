/* ******************************************************************************
 Copyright 2014-2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.restfulapi

import org.grails.web.util.WebUtils
import org.springframework.util.Assert

/**
 * Creates error objects that support V1
 */
class ApiErrorFactory {

    static String V2_ERROR_TYPE = "application/vnd.hedtech.integration.errors.v2+json"
    static String HEADER_RESPONSE_TYPE = "X-Media-Type"

    static def create(String version, String id, String sourceId, String code, String message, String description) {
        Assert.notNull(version)
        Assert.isTrue(version.equals(V2_ERROR_TYPE), "Requested version is not supported")
        //forces use of error header
        WebUtils.retrieveGrailsWebRequest()?.removeAttribute("overwriteMediaTypeHeader", 0)

        if (version?.equals(V2_ERROR_TYPE)) {
            return createVersion2(id, sourceId, code,message, description)
        }
        return null;
    }

    static private def createVersion2(String id, String sourceId, String code, String message, String description) {
        def error = [:]

        Assert.notNull(message)
        Assert.hasLength(message)

        if (id) {
            error['id'] = id;
        }
        if (sourceId) {
            error['sourceId'] = sourceId;
        }
        if (code) {
            error['code'] = code
        }
        if (description) {
            error['description'] = description
        }
        if (message) {
            error['message'] = message
        }

        return ["errors": [error]]
    }

}
