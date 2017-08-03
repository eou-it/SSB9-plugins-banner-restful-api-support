/* ****************************************************************************
Copyright 2017 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
package net.hedtech.integration.extension.exceptions

import net.hedtech.banner.i18n.MessageHelper

/**
 * A runtime exception indicating that a property value does not match the json property type.
 **/
public class JsonPropertyTypeMismatchException extends RuntimeException {

    String jsonPathLabel
    String jsonPropertyType
    String dateFormat

    public String getMessage() {
        if (jsonPropertyType == "S") {
            return MessageHelper.message("jsonPropertyTypeMismatch.string.message", [jsonPathLabel]?.toArray() )
        } else if (jsonPropertyType == "N") {
            return MessageHelper.message("jsonPropertyTypeMismatch.number.message", [jsonPathLabel]?.toArray() )
        } else if (jsonPropertyType in ["D","T"]) {
            return MessageHelper.message("jsonPropertyTypeMismatch.date.message", [jsonPathLabel, dateFormat]?.toArray() )
        } else {
            return MessageHelper.message("jsonPropertyType.invalid.message", [jsonPropertyType, jsonPathLabel]?.toArray())
        }
    }

    String toString() {
        getMessage()
    }

}

