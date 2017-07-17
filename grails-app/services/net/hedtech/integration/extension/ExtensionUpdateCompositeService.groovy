/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import grails.transaction.Transactional
import net.hedtech.banner.service.ServiceBase

@Transactional
class ExtensionUpdateCompositeService extends ServiceBase {

    def extensionDefinitionService
    def updateCompositeService
    def extensionValueExtractionService
    def extractedExtensionPropertyGroupBuilderService
    def resourceIdListBuilderService

    ExtensionProcessResult update(String resourceName, String extensionCode,
                                  def request, Map requestParms, def responseContent) {

        //Create a process result and default the content to be the current response
        ExtensionProcessResult extensionProcessResult = new ExtensionProcessResult()
        extensionProcessResult.content = responseContent

        //Get a list of the extension meta data for the resource, this list effectively is all the new json properties that are
        //to be extracted from the resource(s) in the response
        def extensionDefinitionList = extensionDefinitionService.findAllByResourceNameAndExtensionCode(resourceName,extensionCode)
        if(extensionDefinitionList){
            def extractedExtensionPropertyList = extensionValueExtractionService.extractExtensions(request,extensionDefinitionList)

            //Get a list of resource IDs
            def resourceId
            JsonNode rootContent = getJSONNodeForContent(responseContent)
            def resourceIdList = resourceIdListBuilderService.buildFromContentRoot(rootContent)
            if (resourceIdList){
                resourceId = resourceIdList[0]
            }

            //Now group the properties together to save calls
            def extractedExtensionPropertyGroupList = extractedExtensionPropertyGroupBuilderService.build(extractedExtensionPropertyList)
            if (extractedExtensionPropertyList){
                //Need to deal with results
                updateCompositeService.update(resourceId, extractedExtensionPropertyGroupList)
            }
        }

        return extensionProcessResult
    }

    def getJSONNodeForContent(def responseContent){
        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(responseContent);
        return rootNode
    }


}
