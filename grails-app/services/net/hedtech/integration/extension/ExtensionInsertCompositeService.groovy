/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import grails.transaction.Transactional
import net.hedtech.banner.service.ServiceBase

@Transactional
class ExtensionInsertCompositeService extends ServiceBase {

    def extensionDefinitionService
    def insertCompositeService
    def extensionValueExtractionService
    def extractedExtensionPropertyGroupBuilderService

    ExtensionProcessResult insert(String resourceName, String extensionCode,
                                  def request, Map requestParms, def responseContent) {

        //Create a process result and default the content to be the current response
        ExtensionProcessResult extensionProcessResult = new ExtensionProcessResult()
        extensionProcessResult.content = responseContent

        //Get a list of the extension meta data for the resource, this list effectively is all the new json properties that are
        //to be extracted from the resource(s) in the response
        def extensionDefinitionList = extensionDefinitionService.findAllByResourceNameAndExtensionCode(resourceName,extensionCode)
        if(extensionDefinitionList){
            def extractedExtensionPropertyList = extensionValueExtractionService.extractExtensions(request,extensionDefinitionList)

            //Now group the properties together to save calls
            def extractedExtensionPropertyGroupList = extractedExtensionPropertyGroupBuilderService.build(extractedExtensionPropertyList)
        }

        return extensionProcessResult
    }
}