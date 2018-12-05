/******************************************************************************
 Copyright 2017-2018 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import grails.transaction.Transactional
import net.hedtech.banner.service.ServiceBase
import net.hedtech.restfulapi.RepresentationRequestAttributes

/**
 * The main process entry point for Ethos API extensions
 */
@Transactional
class ExtensionProcessCompositeService extends ServiceBase {

    def extensionReadCompositeService
    def extensionWriteCompositeService
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
    ExtensionProcessResult applyExtensions(String resourceName, def request, Map requestParms, def responseContent, def isQapi) {
        ExtensionProcessResult extensionProcessResult = null

        //Determine if an extension has been defined for this resource
        ExtensionVersion extensionVersion = findExtensionVersionIfExists(resourceName, request)
        if (extensionVersion && request){
            if (isWriteMethod(request, isQapi)){
                extensionProcessResult = extensionWriteCompositeService.write(resourceName,extensionVersion.extensionCode,request.getMethod(),request,responseContent)
            }

            //After the writable operations are done, we need to apply extensions to the response
            if (isWriteMethod(request, isQapi) || isReadMethod(request, isQapi)){
                extensionProcessResult = extensionReadCompositeService.read(resourceName,extensionVersion.extensionCode,responseContent)
            }
        }

        return extensionProcessResult
    }

    /**
     *
     * @param resourceName
     * @param request
     * @return
     */
    ExtensionVersion findExtensionVersionIfExists(String resourceName, def request){
        ExtensionVersion extensionVersion = null;

        //Get the media type from the attached response representation config.
        def representationConfig = request.getAttribute(RepresentationRequestAttributes.RESPONSE_REPRESENTATION)
        String responseMediaType = representationConfig?.apiVersion?.mediaType
        if (!responseMediaType) {
            responseMediaType = representationConfig?.mediaType
        }

        //If a media type was found, then look up to see if there are extensions defined for it.
        if (responseMediaType){
            extensionVersion = extensionVersionService.findByResourceNameAndKnownMediaType(resourceName,responseMediaType)
        }

        return extensionVersion
    }

    /**
     * Returns bool if the request operation is a write operation of some sort
     * @param request
     * @return
     */
    boolean isWriteMethod(def request, def isQapi){
        boolean result = false
        if (request){
            String method = request.getMethod()
            if (method && ((method == "POST" && !isQapi) || method == "PUT" || method == "DELETE")){
                result = true
            }
        }
        return result
    }

    /**
     * Returns boolean if the request is a read operation
     * @param request
     * @return
     */
    boolean isReadMethod(def request, def isQapi){
        boolean result = false
        if (request){
            String method = request.getMethod()
            if (method && (method == "GET" || (method == "POST" && isQapi))){
                result = true
            }
        }
        return result
    }
}
