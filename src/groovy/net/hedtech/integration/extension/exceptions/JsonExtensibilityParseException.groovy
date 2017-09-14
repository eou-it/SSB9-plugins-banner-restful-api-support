/* ****************************************************************************
Copyright 2017 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
package net.hedtech.integration.extension.exceptions

import net.hedtech.banner.i18n.MessageHelper

/**
 * A runtime exception indicating that raw JSON text for extensibility is not parseable.
 **/
public class JsonExtensibilityParseException extends RuntimeException {

    String resourceId
    String jsonPathLabel
    String jsonParseError

    public String getMessage() {
        return MessageHelper.message("jsonExtensibilityParse.message", [resourceId, jsonPathLabel, jsonParseError]?.toArray() )
    }

    String toString() {
        getMessage()
    }

}

