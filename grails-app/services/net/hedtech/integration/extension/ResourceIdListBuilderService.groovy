/******************************************************************************
 Copyright 2017-2020 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/

package net.hedtech.integration.extension

import com.fasterxml.jackson.databind.JsonNode
import net.hedtech.banner.service.ServiceBase

/**
 * Class that loops through the response content and builds a list of guids
 */
import groovy.util.logging.Slf4j
@ Slf4j
class ResourceIdListBuilderService extends ServiceBase {

    /**
     * Given the root node of the content, build and return a list of guids for the resources.
     * @param contentRoot
     * @return
     */
    def buildFromContentRoot(JsonNode contentRoot){

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
     * Add a nodes resource ID to the list
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
