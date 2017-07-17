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
    def extensionInsertCompositeService
    def extensionUpdateCompositeService
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
        String method = request.getMethod()

        //Determine if an extension has been defined for this resource
        ExtensionVersion extensionVersion = findExtensionVersionIfExists(resourceName, request)
        if (extensionVersion && method){

            if (method == "POST"){
                extensionProcessResult = extensionInsertCompositeService.insert(resourceName,extensionVersion.extensionCode,
                request,requestParms,responseContent)
            }else if (method == "PUT"){
                extensionProcessResult = extensionUpdateCompositeService.update(resourceName,extensionVersion.extensionCode,
                        request,requestParms,responseContent)
            }


            //if (method == "GET") {
                extensionProcessResult = extensionReadCompositeService.read(resourceName,extensionVersion.extensionCode,request,requestParms,responseContent)
            //}
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

}
