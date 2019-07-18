/* ****************************************************************************
Copyright 2017 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
package net.hedtech.integration.extension.exceptions

import net.hedtech.banner.i18n.MessageHelper

/**
 * A runtime exception indicating that applying an extensibility json patch failed.
 **/
public class JsonExtensibilityPropertyPatchException extends RuntimeException {

    String resourceId
    String jsonPathLabel
    String jsonPropertyType
    String jsonParseError

    public String getMessage() {
        return MessageHelper.message("jsonExtensibilityPropertyPatch.message", [resourceId, jsonPathLabel, jsonPropertyType, jsonParseError]?.toArray() )
    }

    String toString() {
        getMessage()
    }

}

