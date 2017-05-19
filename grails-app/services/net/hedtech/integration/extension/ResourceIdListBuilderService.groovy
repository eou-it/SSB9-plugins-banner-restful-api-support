/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import groovy.json.JsonSlurper
import net.hedtech.banner.service.ServiceBase

/**
 * Class that loops through the response content and builds a list of guids
 */
class ResourceIdListBuilderService extends ServiceBase {

    /**
     * Builds a list of guids from the request id
     * @param content
     * @return
     */
    def buildFromGuid(String guid) {
        def guidList = null
        if (guid){
            guidList = []
            guidList.add(guid)
        }else{
            log.error "GUID to build resource id list was null"
        }
        return guidList
    }

    /**
     * Builds a list of guids from the response contnet
     * @param content
     * @return
     */
    def buildFromContentList(def content) {
        def guidList = null
        if (content){
            guidList = []
            def parsedJson = new JsonSlurper().parseText(content)
            if (parsedJson){
                parsedJson.each {
                    guidList.add(it.id)
                }
            }else{
                log.error "Error parsing json content"
            }

        }else{
            log.error "Content to build resource id list was null"
        }
        return guidList
    }
}
