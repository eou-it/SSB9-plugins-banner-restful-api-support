/* ****************************************************************************
Copyright 2017 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
package net.hedtech.integration.extension.exceptions

import net.hedtech.banner.i18n.MessageHelper

/**
 * A runtime exception indicating that applying an extensibility json patch failed
 * because the property path is an array and that is not allowed.
 **/
public class JsonExtensibilityArrayPatchException extends RuntimeException {

    String resourceId
    String jsonPathLabel
    String jsonParseError

    public String getMessage() {
        return MessageHelper.message("jsonExtensibilityArrayPatch.message", [resourceId, jsonPathLabel, jsonParseError]?.toArray() )
    }

    String toString() {
        getMessage()
    }

}

