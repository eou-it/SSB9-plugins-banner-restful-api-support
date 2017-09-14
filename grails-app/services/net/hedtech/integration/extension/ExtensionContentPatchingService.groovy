/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonPatch

import java.text.SimpleDateFormat

import net.hedtech.integration.extension.exceptions.JsonExtensibilityParseException
import net.hedtech.integration.extension.exceptions.JsonPropertyTypeMismatchException

/**
 * Class that takes in a list of process results and adds them to the content
 */
class ExtensionContentPatchingService {

    /**
     * Function that patches each resource in the list
     * @param extensionProcessReadResultList
     * @param content
     * @return
     */
    def patchExtensions(def extensionProcessReadResultList, JsonNode rootContent){

        //Stopwatch start
        def startTime = new Date()

        def resultContent = ''
        JsonNode rootNode = rootContent
        if (rootNode.isArray()){
            resultContent = "["
            boolean isFirst = true
            rootNode.each {
                JsonNode idNode  = it.path("id")
                String currentResourceId = idNode.asText()

                if (currentResourceId){
                    //Find all the results for this guid
                    def resourceExtensions = getExtensionsForResourceId(currentResourceId,extensionProcessReadResultList)

                    //call function that takes a JsonNode, a list of results
                    if (!isFirst){
                        resultContent = resultContent + ","
                    }
                    resultContent = resultContent + patchExtensionToResource(it,resourceExtensions)
                }
                isFirst = false
            }
            resultContent = resultContent + "]"
        }else{
            resultContent = patchExtensionToResource(rootNode,extensionProcessReadResultList)
        }

        //Stopwatch stop
        def endTime = new Date()

        log.debug("Ethos Extensions apply json patch numOfItems: ${extensionProcessReadResultList.size}, ms: ${endTime.time - startTime.time}")
        return resultContent
    }


    /**
     * Applies the all of the extensions in the passed in list to the passed in resource
     */
    public String patchExtensionToResource(JsonNode resource, extensionResultList){
        def ObjectMapper MAPPER = new ObjectMapper()
        JsonNode newContent = resource
        Map nestedPaths = findNestedPaths(extensionResultList)
        for (Map.Entry nestedPath in nestedPaths){
            String label = nestedPath.key
            if (!newContent.has(label)) {
                String patch = buildNestedPathsPatch(label, nestedPath.value)
                JsonNode patchNode = MAPPER.readTree(patch)
                newContent = JsonPatch.apply(patchNode, newContent)
            }
        }
        for (ExtensionProcessReadResult extensionProcessReadResult in extensionResultList){
            String patch = buildPatch(extensionProcessReadResult)
            JsonNode patchNode = MAPPER.readTree(patch)
            newContent = JsonPatch.apply(patchNode, newContent)
        }
        return  MAPPER.writeValueAsString(newContent)
    }

    /**
     * Return only the extension results for this resource
     * @param resourceId
     * @param extensionProcessReadResultList
     * @return
     */
    public List<ExtensionProcessReadResult> getExtensionsForResourceId(String resourceId,
                                                                       extensionProcessReadResultList){
        def extensionProcessReadResultListForResource = []
        if (extensionProcessReadResultList) {
            extensionProcessReadResultList.each{
                if (it.resourceId && it.resourceId.equalsIgnoreCase(resourceId)){
                    extensionProcessReadResultListForResource.add(it)
                }
            }
        }
        return extensionProcessReadResultListForResource
    }

    /**
     * Function to build a JSON path label from the definition
     */
    private String buildJsonPathLabel(String jsonPath, String jsonLabel){
        return (jsonPath?.startsWith("/") ? "" : "/") +
                jsonPath +
                (jsonPath?.endsWith("/") ? "" : "/") +
                jsonLabel?.replaceAll("/", "")
    }

    /**
     * Function to build a JSON Patch string used to modify the JSON resource
     * This method only assumes an add operation and expects a path, label and value
     */
    public String buildPatch(ExtensionProcessReadResult extensionProcessReadResult){
        String patch = null
        if (extensionProcessReadResult
                && extensionProcessReadResult.jsonPath
                && extensionProcessReadResult.jsonLabel)
        {
            def value = extensionProcessReadResult.value

            // get the json label and property type for mismatch validation
            String jsonPathLabel = buildJsonPathLabel(extensionProcessReadResult.jsonPath, extensionProcessReadResult.jsonLabel)
            String jsonPropertyType = extensionProcessReadResult.jsonPropertyType

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
                    } else if (jsonPropertyType == "J" && !(value instanceof String)) {
                        throw new JsonPropertyTypeMismatchException(jsonPathLabel: jsonPathLabel, jsonPropertyType: jsonPropertyType)
                    }
                }
            }

            //Build a patch for a number if the type is number, else default to a string
            // - we also check for date and timestamp which will be formatted as a string
            // - allow raw JSON text to be patched as-is (just like a number)
            if (extensionProcessReadResult.jsonPropertyType in ["N","J"]) {
                patch = '[{"op":"add","path":"' + jsonPathLabel + '","value":' + value + '}]';
                // validate raw JSON text
                if (extensionProcessReadResult.jsonPropertyType == "J") {
                    try {
                        new ObjectMapper().readTree(value);
                    } catch (Throwable t) {
                        throw new JsonExtensibilityParseException(resourceId: extensionProcessReadResult.resourceId, jsonPathLabel: jsonPathLabel, jsonParseError: t.getMessage())
                    }
                }
            } else {
                // check for dates
                if (extensionProcessReadResult.jsonPropertyType == "D" && value instanceof Date) {
                    def dateFormatter = new SimpleDateFormat(ExtensionConstants.DATE_FORMAT)
                    dateFormatter.setLenient(false)
                    value = dateFormatter.format(value)
                }
                // check for timestamps
                if (extensionProcessReadResult.jsonPropertyType == "T" && value instanceof Date) {
                    def dateFormatter = new SimpleDateFormat(ExtensionConstants.TIMESTAMP_FORMAT_WITHOUT_TIMEZONE)
                    dateFormatter.setLenient(false)
                    dateFormatter.setTimeZone(TimeZone.getTimeZone('UTC'))
                    value = dateFormatter.format(value)+"+00:00"
                }
                // default to string formatting
                patch = '[{"op":"add","path":"' + jsonPathLabel + '","value":"' + value + '"}]';
            }


        }
        return patch
    }

    /**
     * Function to find nested paths in the extensions results
     */
    public Map findNestedPaths(extensionResultList){
        Map nestedPaths = [:]
        if (extensionResultList) {
            for (ExtensionProcessReadResult extensionProcessReadResult in extensionResultList) {
                if (extensionProcessReadResult.jsonPath && extensionProcessReadResult.jsonLabel) {
                    String jsonPathLabel = buildJsonPathLabel(extensionProcessReadResult.jsonPath, extensionProcessReadResult.jsonLabel)
                    if (jsonPathLabel.count("/") > 1) {
                        List<String> paths = jsonPathLabel.tokenize("/")
                        findNestedPathsHelper(nestedPaths, paths.head(), paths.tail())
                    }
                }
            }
        }
        return nestedPaths
    }

    private void findNestedPathsHelper(Map nestedPaths, String label, List<String> paths){
        Map nestedPath = nestedPaths.get(label)
        if (nestedPath == null) {
            nestedPath = [:]
            nestedPaths.put(label, nestedPath)
        }
        if (paths.size() > 1) {
            findNestedPathsHelper(nestedPath, paths.head(), paths.tail())
        }
    }

    /**
     * Function to build a JSON Patch string used to modify the JSON resource
     * This method is used to create empty complex fields to apply patches for
     * nested data where the complex fields do not already exist in the resource
     */
    public String buildNestedPathsPatch(String label, Map nestedPaths){
        def value = buildNestedPathsPatchHelper(nestedPaths)
        return '[{"op":"add","path":"' + label + '","value":{' + value + '}}]';
    }

    private String buildNestedPathsPatchHelper(Map nestedPaths){
        def value = ''
        if (nestedPaths) {
            for (Map.Entry nestedPath in nestedPaths) {
                if (value.size() > 0) value += ','
                value += '"' + nestedPath.key + '":{' + buildNestedPathsPatchHelper(nestedPath.value) + '}'
            }
        }
        return value
    }
}
