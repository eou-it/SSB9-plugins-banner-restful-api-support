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
    def representationResolutionService

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
    ExtensionProcessResult applyExtensions(String resourceName, def request, Map requestParms, def responseContent) {
        ExtensionProcessResult extensionProcessResult = null
        String method = request.getMethod()

        if (isRequestForCatalogResource(request)){
            ExtensionVersion extensionVersion = findExtensionVersionIfExists(resourceName, request)
            if (extensionVersion){
                //Get the code for this
                if (method && method == "GET") {
                    extensionProcessResult = extensionReadCompositeService.read(resourceName,extensionVersion.extensionCode,request,requestParms,responseContent)
                    if (extensionProcessResult){
                        extensionProcessResult.catalogId = extensionVersion.alias
                        extensionProcessResult.catalogHeaderName = representationResolutionService.getCatalogResponseHeaderName()
                    }

                }
            }
        }


        return extensionProcessResult
    }

    /**
     *
     * @param request
     * @return
     */
    boolean isRequestForCatalogResource(def request){
        boolean result = true

        //Has non versionless Accept
        String requestAcceptHeader
        Enumeration<String> values = request.getHeaders("Accept")
        if (values) {
            while (values.hasMoreElements()) {
                requestAcceptHeader = values.nextElement()
                if (requestAcceptHeader.contains("vnd.hedtech.integration")){
                    result = false
                    return result
                }
            }

        } else {
            requestAcceptHeader= request?.getHeader("Accept")
        }

        if (requestAcceptHeader.contains("vnd.hedtech.integration")){
            result = false
        }
        return result
    }

    /**
     *
     * @param resourceName
     * @param request
     * @return
     */
    ExtensionVersion findExtensionVersionIfExists(String resourceName, def request){
        ExtensionVersion extensionVersion = null;
        String catalog = representationResolutionService.getRequestCatalog(request)
        if (catalog){
            //We were given a catalog so lookup to see if there is an extended version
            extensionVersion = extensionVersionService.findByAliasAndResourceName(catalog,resourceName)
        }else{

            extensionVersion = extensionVersionService.findDefaultByResourceName(resourceName)
        }
        return extensionVersion
    }

}
