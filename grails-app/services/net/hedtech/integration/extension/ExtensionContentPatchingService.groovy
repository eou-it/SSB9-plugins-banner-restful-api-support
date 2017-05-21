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


    def patchExtensionToResource(JsonNode resource, extensionResultList){
        def ObjectMapper MAPPER = new ObjectMapper()
        JsonNode newContent = resource
        for (ExtensionProcessReadResult extensionProcessReadResult in extensionResultList){
            String patch = buildPatchString(extensionProcessReadResult)
            JsonNode patchNode = MAPPER.readTree(patch)
            newContent = JsonPatch.apply(patchNode, newContent)
        }
        return  MAPPER.writeValueAsString(newContent)
    }

    def getExtensionsForResourceId(String resourceId, extensionProcessReadResultList){
        def extensionProcessReadResultListForResource = []
        if (extensionProcessReadResultList) {
            extensionProcessReadResultList.each{
                if (it.resourceId){
                    if (it.resourceId.equalsIgnoreCase(resourceId)){
                        extensionProcessReadResultListForResource.add(it)
                    }
                }
            }
        }
        return extensionProcessReadResultListForResource
    }

    //Build a JSON Patch string for the work
    def buildPatchString(ExtensionProcessReadResult extensionProcessReadResult){

        def patchedValue = extensionProcessReadResult.value
        def patchString = '[{"op": "add","path": "' + extensionProcessReadResult.jsonPath +
                extensionProcessReadResult.jsonLabel + '","value":"' + patchedValue + '"}]';


        return patchString

    }
}
