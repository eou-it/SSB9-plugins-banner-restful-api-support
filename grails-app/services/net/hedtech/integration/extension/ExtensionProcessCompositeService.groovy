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
    def extensionWriteCompositeService
    def extensionVersionService

    boolean transactional = true

    private static final String RESPONSE_REPRESENTATION = 'net.hedtech.restfulapi.RestfulApiController.response_representation'

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

        //Determine if an extension has been defined for this resource
        ExtensionVersion extensionVersion = findExtensionVersionIfExists(resourceName, request)
        if (extensionVersion && request){
            if (isWriteMethod(request)){
                extensionProcessResult = extensionWriteCompositeService.write(resourceName,extensionVersion.extensionCode,
                        request,requestParms,responseContent)
            }

            //After the writable operations are done, we need to apply extensions to the response
            if (isWriteMethod(request) || isReadMethod(request)){
                extensionProcessResult = extensionReadCompositeService.read(resourceName,extensionVersion.extensionCode,request,requestParms,responseContent)
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

        //First see if the overwritten media type is there.
        //This is ONLY set when application\json is passed in. The API services code puts the REAL latest media
        //type in this attribute. If it is null, then look at the representation config attached.
        String responseMediaType = request.getAttribute("overwriteMediaTypeHeader");
        if (!responseMediaType){
            def representationConfig = request.getAttribute(RESPONSE_REPRESENTATION)
            responseMediaType = representationConfig.mediaType
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
    boolean isWriteMethod(def request){
        boolean result = false
        if (request){
            String method = request.getMethod()
            if (method && method == "POST" || method == "PUT" || method == "DELETE"){
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
    boolean isReadMethod(def request){
        boolean result = false
        if (request){
            String method = request.getMethod()
            if (method && method == "GET"){
                result = true
            }
        }
        return result
    }
}
