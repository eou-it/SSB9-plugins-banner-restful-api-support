/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonPatch

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
    def patchExtensions(def extensionProcessReadResultList, def content){
        def resultContent = ''
        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(content);

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
            String patchedValue = extensionProcessReadResult.value
            patch = '[{"op":"add","path": "' + extensionProcessReadResult.jsonPath +
                    extensionProcessReadResult.jsonLabel + '","value":"' + patchedValue + '"}]';

        }
        return patch
    }
}
