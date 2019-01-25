/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension

import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.ReadContext
import grails.converters.JSON
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

import java.text.SimpleDateFormat

import net.hedtech.integration.extension.exceptions.JsonPropertyTypeMismatchException

class ExtensionValueExtractionService {

    /**
     * Give a request and list of Extension Defintions
     *  Extract the values out of the request and return a list of them
     * @param request
     * @param extensionDefinitionList
     * @return
     */
    List<ExtractedExtensionProperty> extractExtensions(def request, def extensionDefinitionList){
        List<ExtractedExtensionProperty> extractedExtensionPropertyList = []

        if (request && extensionDefinitionList && extensionDefinitionList.size > 0){

            //Parse the JSON request body one time, only
            def jsonContent = JSON.parse(request);
            ReadContext requestReadContext = JsonPath.parse( jsonContent);

            //Loop through the Extension Definition and extract the values in the request by path
            for (ExtensionDefinition extensionDefinition : extensionDefinitionList) {

                def jsonPathToValue = buildJsonPathFromPatchPath(buildJsonPatchPath(extensionDefinition))
                def value

                //Set new property
                ExtractedExtensionProperty extractedExtensionProperty = new ExtractedExtensionProperty()
                extractedExtensionProperty.extensionDefinition = extensionDefinition

                try {
                    value = requestReadContext.read(jsonPathToValue)
                }catch (Exception exc){
                    //Really nothing to do here now...just swallow it and move on (the value) will not be saved
                    extractedExtensionProperty.valueWasMissing = true
                }

                // get the json label and property type for mismatch validation
                String jsonPathLabel = extensionDefinition.jsonPath +
                        (extensionDefinition.jsonPath?.endsWith("/") ? "" : "/") +
                        extensionDefinition.jsonLabel
                String jsonPropertyType = extensionDefinition.jsonPropertyType

                // must convert date or timestamp string to a real date value
                if (jsonPropertyType in ["D","T"] && value instanceof String) {
                    String format = (jsonPropertyType == "D" ? ExtensionConstants.DATE_FORMAT : ExtensionConstants.TIMESTAMP_FORMAT)
                    def dateFormatter = new SimpleDateFormat(format)
                    dateFormatter.setLenient(false)
                    if (jsonPropertyType == "T") {
                        dateFormatter.setTimeZone(TimeZone.getTimeZone('UTC'))
                    }
                    try {
                        value = dateFormatter.parse(value)
                    } catch (Throwable t) {
                        throw new JsonPropertyTypeMismatchException(jsonPathLabel: jsonPathLabel, jsonPropertyType: jsonPropertyType, dateFormat: format)
                    }
                }

                // validate that the property value matches the defined json property type
                if (!(jsonPropertyType in ["S","N","D","T","J"])) {
                    throw new JsonPropertyTypeMismatchException(jsonPathLabel: jsonPathLabel, jsonPropertyType: jsonPropertyType)
                } else {
                    if (value != null) {
                        if (jsonPropertyType == "S" && !(value instanceof String)) {
                            throw new JsonPropertyTypeMismatchException(jsonPathLabel: jsonPathLabel, jsonPropertyType: jsonPropertyType)
                        } else if (jsonPropertyType == "N" && !(value instanceof Number)) {
                            throw new JsonPropertyTypeMismatchException(jsonPathLabel: jsonPathLabel, jsonPropertyType: jsonPropertyType)
                        } else if (jsonPropertyType == "D" && !(value instanceof Date)) {
                            throw new JsonPropertyTypeMismatchException(jsonPathLabel: jsonPathLabel, jsonPropertyType: jsonPropertyType, dateFormat: ExtensionConstants.DATE_FORMAT)
                        } else if (jsonPropertyType == "T" && !(value instanceof Date)) {
                            throw new JsonPropertyTypeMismatchException(jsonPathLabel: jsonPathLabel, jsonPropertyType: jsonPropertyType, dateFormat: ExtensionConstants.TIMESTAMP_FORMAT)
                        } else if (jsonPropertyType == "J" && !(value instanceof JSONObject || value instanceof JSONArray)) {
                            throw new JsonPropertyTypeMismatchException(jsonPathLabel: jsonPathLabel, jsonPropertyType: jsonPropertyType)
                        }
                    }
                }

                // convert JSON objects to raw JSON text for persistance in the database
                if (jsonPropertyType == "J") {
                    value = value.toString()
                }

                // property value has been validated
                extractedExtensionProperty.value = value
                extractedExtensionPropertyList.add(extractedExtensionProperty)
            }
        }
        return extractedExtensionPropertyList
    }

    /**
     * Build a path string from the Extension Definition
     * @param extensionDefinition
     * @return
     */
    String buildJsonPatchPath (ExtensionDefinition extensionDefinition)
    {
        String jsonPatchPath = ""
        if (extensionDefinition){
            jsonPatchPath = extensionDefinition.jsonPath
            if (!jsonPatchPath.endsWith("/")){
                jsonPatchPath = jsonPatchPath + "/"
            }
            jsonPatchPath = jsonPatchPath + extensionDefinition.jsonLabel
        }

        return jsonPatchPath
    }

    /**
     * Take a JSON Path string and turn it into a JSONPath for our library to extract
     * @param jsonPatchPath
     * @return
     */
    String buildJsonPathFromPatchPath(String jsonPatchPath){
        String jsonPath
        if (jsonPatchPath){
            jsonPath = jsonPatchPath.replace("/",".")
            jsonPath = '$' + jsonPath
        }
        return jsonPath
    }
}
