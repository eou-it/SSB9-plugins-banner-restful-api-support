/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension

import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.ReadContext
import grails.converters.JSON

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
                extractedExtensionProperty.extendedDefinition = extensionDefinition

                try {
                    value = requestReadContext.read(jsonPathToValue)
                }catch (Exception exc){
                    //Really nothing to do here now...just swallow it and move on (the value) will not be saved
                    extractedExtensionProperty.valueWasMissing = true
                }
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
