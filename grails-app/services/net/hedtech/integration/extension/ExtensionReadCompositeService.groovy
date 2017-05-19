/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import grails.transaction.Transactional
import net.hedtech.banner.service.ServiceBase

@Transactional
class ExtensionReadCompositeService extends ServiceBase {

    def extensionContentPatchingService
    def extensionDefinitionService
    def readCompositeService

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
        ExtensionProcessResult ethosExtensionResult = new ExtensionProcessResult()
        ethosExtensionResult.content = responseContent

        //Get a list of the extension meta data for the resource
        def extensionDefinitionList = extensionDefinitionService.findAllByResourceNameAndExtensionCode(resourceName,extensionCode)
        if(extensionDefinitionList){
            //Call a service to read from the datasource(s)
            def extensionProcessReadResultList = readCompositeService.read(extensionDefinitionList,requestParms,responseContent)
            if (extensionProcessReadResultList){
                //Call a service to apply the new extensions to the response
                def extendedContent = extensionContentPatchingService.patchExtensions(extensionDefinitionList,responseContent)
                if (extendedContent){
                    ethosExtensionResult.content=extendedContent
                    ethosExtensionResult.wasExtended=true
                }
            }
        }
        return ethosExtensionResult
    }

}
