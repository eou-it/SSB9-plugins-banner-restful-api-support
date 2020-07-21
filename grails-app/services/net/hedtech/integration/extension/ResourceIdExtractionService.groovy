/******************************************************************************
 Copyright 2017-2020 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import com.fasterxml.jackson.databind.JsonNode

/**
 * Class to extract the resource identifier (GUID) from the json content
 */
import groovy.util.logging.Slf4j
@ Slf4j
class ResourceIdExtractionService {

    /**
     *
     * @param contentRoot
     * @return
     */
    def extractIdFromNode(JsonNode contentRoot){
        def resourceId
        def resourceIdList = extractIdListFromNode(contentRoot)
        if (resourceIdList){
            resourceId = resourceIdList[0]
        }
        return resourceId
    }

    /**
     * Given the root node of the content, build and return a list of guids for the resources.
     * @param contentRoot
     * @return
     */
    def extractIdListFromNode(JsonNode contentRoot){

        //Stopwatch start
        def startTime = new Date()

        def returnIdList = null
        if (contentRoot != null){
            returnIdList = []
            if (contentRoot.isArray()){
                contentRoot.each {
                    returnIdList = addIdToList(returnIdList,it)
                }
            }else{
                returnIdList = addIdToList(returnIdList,contentRoot)
            }
        }
        //Stopwatch stop
        def endTime = new Date()

        log.debug("Ethos Extensions getting a list of guids took ms: ${endTime.time - startTime.time}")

        return returnIdList
    }

    /**
     * Add a nodes resource ID to the list.  This assume the label ID
     * @param resourceIdList
     * @param resourceNode
     * @return
     */
    private addIdToList(def resourceIdList, JsonNode resourceNode){
        if (resourceIdList != null && resourceNode != null){
            String currentResourceId = resourceNode.get("id")?.textValue()
            if (currentResourceId ){
                resourceIdList.add(currentResourceId)
            }
        }
        return resourceIdList
    }
}


