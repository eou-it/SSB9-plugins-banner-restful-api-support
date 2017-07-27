/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonPatch

import java.text.SimpleDateFormat

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
     * Function to build a JSON Patch string used to modify the JSON resource
     * This method only assumes an add operation and expects a path, label and value
     *
     *
     * At some point to support adding a container...see this patch format
     *
                 { "foo": "bar" }

                 A JSON Patch document:

                 [
                 { "op": "add", "path": "/child", "value": { "grandchild": { } } }
                 ]

                 The resulting JSON document:

                 {
                   "foo": "bar",
                   "child": {
                        "grandchild": {
                        }
                   }
                 }

     */
    public String buildPatch(ExtensionProcessReadResult extensionProcessReadResult){
        String patch = null
        if (extensionProcessReadResult
                && extensionProcessReadResult.jsonPath
                && extensionProcessReadResult.jsonLabel)
        {
            def patchedValue = extensionProcessReadResult.value
            String jsonPathLabel = extensionProcessReadResult.jsonPath +
                    (extensionProcessReadResult.jsonPath?.endsWith("/") ? "" : "/") +
                    extensionProcessReadResult.jsonLabel

            //Build a patch for a number if the type is number, else default to a string
            // - we also check for date and timestamp which will be formatted as a string
            if (extensionProcessReadResult.jsonPropertyType == "N") {
                patch = '[{"op":"add","path":"' + jsonPathLabel + '","value":' + patchedValue + '}]';
            }else{
                // check for dates
                if (extensionProcessReadResult.jsonPropertyType == "D" && patchedValue instanceof Date) {
                    patchedValue = new SimpleDateFormat("yyyy-MM-dd").format(patchedValue)
                }
                // check for timestamps
                if (extensionProcessReadResult.jsonPropertyType == "T" && patchedValue instanceof Date) {
                    def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    dateFormatter.setTimeZone(TimeZone.getTimeZone('UTC'))
                    patchedValue = dateFormatter.format(patchedValue)+"+00:00"
                }
                // default to string formatting
                patch = '[{"op":"add","path":"' + jsonPathLabel + '","value":"' + patchedValue + '"}]';
            }


        }
        return patch
    }
}
