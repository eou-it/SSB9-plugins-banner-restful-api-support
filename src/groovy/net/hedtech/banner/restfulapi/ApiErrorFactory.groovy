/* ******************************************************************************
 Copyright 2014-2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.restfulapi

/**
 * Creates error objects that support V1
 */
class ApiErrorFactory {

    static String V1_ERROR_TYPE = "application/vnd.hedtech.integration.errors.v1+json"
    static String NO_VERSION_ERROR_TYPE = ""
    static String HEADER_RESPONSE_TYPE="X-Error-Media-Format"

    static def create(String version, String id, String sourceId, String code, String title, String description) {

        if (version?.equals(V1_ERROR_TYPE)) {
            return createV1(id, sourceId, code, title, description)
        }
        if(version?.equals(NO_VERSION_ERROR_TYPE))
        {
            return createNoVersion(id, sourceId, code, title, description)
        }

    }

    static def createNoVersion(String id, String sourceId, String code, String title, String description) {
        def error = [:]


        if (code) {
            error['code'] = code
        }
        if (title) {
            error['message'] = title
        }
        if (description) {
            error['description'] = description
        }
        return error
    }


    static def createV1(String id, String sourceId, String code, String title, String description) {
        def error = [:]


        if (id) {
            error['id'] = id;
        }
        if (sourceId) {
            error['sourceId'] = sourceId;
        }
        if (code) {
            error['code'] = code
        }
        if (title) {
            error['title'] = title
        }
        if (description) {
            error['description'] = description
        }
        return error
    }

}
