/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import grails.gorm.transactions.Transactional
import net.hedtech.banner.service.ServiceBase

@Transactional
class ExtensionWriteCompositeService extends ServiceBase {

    def extensionDefinitionService
    def writeCompositeService
    def extensionValueExtractionService
    def extractedExtensionPropertyGroupBuilderService
    def resourceIdExtractionService
    def responseJsonNodeBuilderService

    /**
     * Write function to apply content extensions
     * @param resourceName
     * @param extensionCode
     * @param request
     * @param responseContent
     * @return
     */
    ExtensionProcessResult write(String resourceName, String extensionCode, String httpMethod, def request, def responseContent) {

        //Create a process result and default the content to be the current response
        ExtensionProcessResult extensionProcessResult = new ExtensionProcessResult()
        extensionProcessResult.content = responseContent

        //Get a list of the extension meta data for the resource, this list effectively is all the new json properties that are
        //to be extracted from the resource(s) in the response
        def extensionDefinitionList = extensionDefinitionService.findAllByResourceNameAndExtensionCode(resourceName,extensionCode)
        if(extensionDefinitionList){
            def extractedExtensionPropertyList = extensionValueExtractionService.extractExtensions(request,extensionDefinitionList)
            def resourceId = getResourceIdFromResponse(responseContent)

            //Now group the properties together to save calls
            def extractedExtensionPropertyGroupList = extractedExtensionPropertyGroupBuilderService.build(extractedExtensionPropertyList)
            if (extractedExtensionPropertyGroupList){
                //Need to deal with results
                writeCompositeService.write(resourceId,httpMethod,extractedExtensionPropertyGroupList)
            }
        }

        return extensionProcessResult
    }

    /**
     * Get the ResourceID from the response content
     * @param responseContent
     * @return
     */
    def getResourceIdFromResponse(def responseContent){
        //We need to extract the GUID from the New or Updated resource in the response
        JsonNode rootContent = responseJsonNodeBuilderService.build(responseContent)
        return resourceIdExtractionService.extractIdFromNode(rootContent)
    }

}
