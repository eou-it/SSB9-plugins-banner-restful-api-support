/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import grails.transaction.Transactional
import net.hedtech.banner.service.ServiceBase

@Transactional
class ExtensionReadCompositeService extends ServiceBase {

    def extensionContentPatchingService
    def extensionDefinitionService
    def readCompositeService
    def resourceIdListBuilderService

    /**
     * Read function to apply content extensions
     * @param resourceName
     * @param catalog
     * @param request
     * @param requestParms
     * @param responseContent
     * @return
     */
    ExtensionProcessResult read(String resourceName, String extensionCode, def request, Map requestParms, def responseContent) {

        //Create a process result and default the content to be the current response
        ExtensionProcessResult ethosExtensionResult = new ExtensionProcessResult()
        ethosExtensionResult.content = responseContent

        //Get a list of the extension meta data for the resource, this list effectively is all the new json properties that are
        //to be added to the resource(s) in the response
        def extensionDefinitionList = extensionDefinitionService.findAllByResourceNameAndExtensionCode(resourceName,extensionCode)
        if(extensionDefinitionList){
            JsonNode rootContent = getJSONNodeForContent(responseContent)
            def resourceIdList = resourceIdListBuilderService.buildFromContentRoot(rootContent)

            //Call a SQL based service to read from the datasource(s)
            def extensionProcessReadResultList = readCompositeService.read(extensionDefinitionList,resourceIdList)
            if (extensionProcessReadResultList){
                //Call a service to apply the new extensions and values to the response
                def extendedContent = extensionContentPatchingService.patchExtensions(extensionProcessReadResultList,rootContent)
                if (extendedContent){
                    ethosExtensionResult.content=extendedContent
                    ethosExtensionResult.wasExtended=true
                }else{
                    log.error "There was an error in applying all the patches to the resource."
                }
            }else{
                log.error "Extension results where expected, given there is meta data setup for the extension."
            }
        }
        return ethosExtensionResult
    }

    def getJSONNodeForContent(def responseContent){
        def ObjectMapper MAPPER = new ObjectMapper();
        JsonNode rootNode = MAPPER.readTree(responseContent);
        return rootNode
    }

}
