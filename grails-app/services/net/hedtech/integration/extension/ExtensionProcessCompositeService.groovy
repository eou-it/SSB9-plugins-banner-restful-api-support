/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import grails.transaction.Transactional
import net.hedtech.banner.service.ServiceBase

/**
 * The main process entry point for Ethos API extensions
 */
@Transactional
class ExtensionProcessCompositeService extends ServiceBase {

    def extensionReadCompositeService
    def extensionVersionService

    boolean transactional = true

    /**
     * Main process function that applies exentions to operations
     * @param resourceName
     * @param catalog
     * @param request
     * @param requestParms
     * @param responseContent
     * @return
     */
    ExtensionProcessResult applyExtensions(String resourceName, String catalog, def request, Map requestParms, def responseContent) {
        ExtensionProcessResult extensionProcessResult = null
        String method = request.getMethod()

        ExtensionVersion extensionVersion = extensionVersionService.findByAliasAndResourceName(catalog,resourceName)
        if (extensionVersion){
            //Get the code for this
            if (method && method == "GET") {
                extensionProcessResult = extensionReadCompositeService.read(resourceName,extensionVersion.extensionCode,request,requestParms,responseContent)
            }
        }

        return extensionProcessResult
    }

}
