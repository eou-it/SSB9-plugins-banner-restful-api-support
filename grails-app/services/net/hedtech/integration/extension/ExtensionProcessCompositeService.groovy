/******************************************************************************
 Copyright 2017-2020 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import grails.gorm.transactions.Transactional
import net.hedtech.banner.service.ServiceBase
import net.hedtech.restfulapi.RepresentationRequestAttributes
import net.hedtech.restfulapi.apiversioning.BasicApiVersionParser

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

        //Get all extension versions for the resource - return immediately if none are defined
        List<ExtensionVersion> extensionVersionList = extensionVersionService.fetchAllByResourceName(resourceName)
        if (!(extensionVersionList?.size() > 0)) {
            return extensionProcessResult
        }

        //Determine if an extension has been defined for this resource
        if (request) {
            if (isWriteMethod(request, isQapi)) {
                def representationConfig = request.getAttribute(RepresentationRequestAttributes.REQUEST_REPRESENTATION)
                ExtensionVersion extensionVersion = findExtensionVersionMatch(resourceName, representationConfig, extensionVersionList)
                if (extensionVersion) {
                    extensionProcessResult = extensionWriteCompositeService.write(resourceName, extensionVersion.extensionCode, request.getMethod(), request, responseContent)
                }
            }

            //After the writable operations are done, we need to apply extensions to the response
            if (isWriteMethod(request, isQapi) || isReadMethod(request, isQapi)) {
                def representationConfig = request.getAttribute(RepresentationRequestAttributes.RESPONSE_REPRESENTATION)
                ExtensionVersion extensionVersion = findExtensionVersionMatch(resourceName, representationConfig, extensionVersionList)
                if (extensionVersion) {
                    extensionProcessResult = extensionReadCompositeService.read(resourceName, extensionVersion.extensionCode, responseContent)
                }
            }
        }

        return extensionProcessResult
    }

    /**
     *
     * @param resourceName
     * @param representationConfig
     * @param extensionVersionList
     * @return
     */
    ExtensionVersion findExtensionVersionMatch(String resourceName, def representationConfig, List<ExtensionVersion> extensionVersionList){
        ExtensionVersion extensionVersion = null

        //Get the media type from the attached response representation config.
        String responseMediaType = representationConfig?.apiVersion?.mediaType
        if (!responseMediaType) {
            responseMediaType = representationConfig?.mediaType
        }

        //If a media type was found, then look up to see if there are extensions defined for it.
        if (responseMediaType){
            extensionVersion = getPrioritizedExtensionVersion(resourceName, responseMediaType, extensionVersionList)
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

    /**
     * Returns the prioritized extension version from list.
     * @param resourceName
     * @param responseMediaType
     * @param extensionVersionList
     * @return
     */
    ExtensionVersion getPrioritizedExtensionVersion(String resourceName, String responseMediaType, List<ExtensionVersion> extensionVersionList) {
        // the priority order is:
        //  1) use full matching semantic version of a resource extension
        //  2) use matching major version of a resource extension
        //  3) use application/json media type if defined
        ExtensionVersion extensionVersion = null
        for (ExtensionVersion checkVersion : extensionVersionList) {
            if (checkVersion.knownMediaType == responseMediaType) {
                return checkVersion
            } else if (!extensionVersion && checkVersion.knownMediaType == 'application/json') {
                extensionVersion = checkVersion
            } else {
                // check for match on major version
                def checkApiVersion = new BasicApiVersionParser().parseMediaType(resourceName, checkVersion.knownMediaType)
                if (checkApiVersion.majorVersion != -1 && (checkApiVersion.minorVersion == -1 || checkApiVersion.patchVersion == -1)) {
                    def responseApiVersion = new BasicApiVersionParser().parseMediaType(resourceName, responseMediaType)
                    if (responseApiVersion.majorVersion == checkApiVersion.majorVersion) {
                        extensionVersion = checkVersion
                    }
                }
            }
        }

        return extensionVersion
    }

}
