/******************************************************************************
 Copyright 2017-2019 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.resource

import grails.util.Holders
import net.hedtech.restfulapi.Methods
import net.hedtech.restfulapi.PagedResultArrayList
import net.hedtech.restfulapi.ResourceDetail
import net.hedtech.restfulapi.ResourceDetailList

/**
 * A service to return the list of resources supported by an application.
 **/
public class SupportedResourceService {

    // custom sort order for listing http methods
    def customHttpMethodSorter = { a, b, order = Methods.getAllHttpMethods()*.toLowerCase() ->
        order.indexOf(a) <=> order.indexOf(b)
    }

    /**
     * GET /api/resources
     *
     * @param params Request parameters
     * @return
     */
    def list(Map params) {
        List<SupportedResource> supportedResources = processResourceDetailList()
        return new PagedResultArrayList(supportedResources, supportedResources.size())
    }

    /**
     * GET /api/resources
     *
     * The count method must return the total number of instances of the resource.
     * It is used in conjunction with the list method when returning a list of resources.
     * RestfulApiController will make call to "count" only if the "list" execution happens without any exception.
     *
     * @param params Request parameters
     * @return
     */
    def count(params) {
        return processResourceDetailList().size()
    }

    /**
     * Return the resources supported by the application. Only json media types are returned.
     */
    private List<SupportedResource> processResourceDetailList() {

        // get the bean that was initialized by the RestfulApiController
        ResourceDetailList resourceDetailList = Holders.grailsApplication.mainContext.getBean("resourceDetailList")

        // get the list of resources to be excluded from the response
        String[] excludedResources = (Holders.config.supportedResource.excludedResources ?: [])

        // add an entry to the response for each configured resouce that meets our criteria
        List<SupportedResource> supportedResources = []
        resourceDetailList.resourceDetails.each() { resourceDetail ->

            // only include non-excluded resources that support json
            if (!isExcludedResource(resourceDetail, excludedResources)) {

                // initialize the entry for a resource
                SupportedResource supportedResource = new SupportedResource()
                supportedResource.name = resourceDetail.name

                // add this entry
                supportedResources.add(supportedResource)

                // add all representations of the resource that meet our criteria
                resourceDetail.mediaTypes.each() { mediaType ->
                    if (isMediaTypeJson(mediaType)) {
                        SupportedRepresentation supportedRepresentation = new SupportedRepresentation()
                        supportedRepresentation.mediaType = mediaType
                        Map representationMetadata = resourceDetail.representationMetadata.get(mediaType)
                        supportedRepresentation.methods = findSupportedHttpMethods(resourceDetail, mediaType,representationMetadata)
                        supportedResource.representations.add(supportedRepresentation)

                        // check for representation metadata
                        if (representationMetadata != null) {

                            // check for representation metadata: filters
                            if (representationMetadata.containsKey("filters")) {
                                supportedRepresentation.filters = representationMetadata.get("filters")
                            }

                            // check for getAllPatterns
                            if (representationMetadata.containsKey("getAllPatterns")) {
                                supportedRepresentation.getAllPatterns = new ArrayList<>()
                                representationMetadata.get("getAllPatterns").each {
                                    SupportedPattern pattern = new SupportedPattern()
                                    pattern.name = it.name
                                    pattern.method = it.method
                                    pattern.mediaType = it.mediaType
                                    supportedRepresentation.getAllPatterns.add(pattern)
                                }
                            }

                            // check for representation metadata: namedQueries
                            if (representationMetadata.containsKey("namedQueries")) {
                                supportedRepresentation.namedQueries = new ArrayList<>()
                                for (Map.Entry entry : representationMetadata.get("namedQueries").entrySet()) {
                                    NamedQuery namedQuery = new NamedQuery()
                                    namedQuery.name = entry.key
                                    namedQuery.filters = entry.value.get("filters")
                                    supportedRepresentation.namedQueries.add(namedQuery)
                                }
                            }

                            // check for representation metadata: deprecationNotice
                            if (representationMetadata.containsKey("deprecationNotice")) {
                                Map map = representationMetadata.get("deprecationNotice")
                                DeprecationNotice deprecationNotice = new DeprecationNotice()
                                deprecationNotice.setDeprecatedOn(map.get("deprecatedOn"))
                                deprecationNotice.setSunsetOn(map.get("sunsetOn"))
                                deprecationNotice.setDescription(map.get("description"))
                                supportedRepresentation.deprecationNotice = deprecationNotice
                            }
                        }
                    }
                }
            }
        }

        // return the list of supported resources
        return supportedResources
    }

    /**
     * Return true if the resource is to be excluded from the response.
     *
     * This is externally configured through the supportedResource.excludedResources
     * config property. A resource is also excluded if it does not support json as
     * one of its media types.
     */
    private boolean isExcludedResource(ResourceDetail resourceDetail, String[] excludedResources) {
        if (excludedResources.contains(resourceDetail.name)) {
            return true
        }
        boolean resourceSupportsJson = false
        resourceDetail.mediaTypes.each() { mediaType ->
            if (isMediaTypeJson(mediaType)) {
                resourceSupportsJson = true
            }
        }
        return !resourceSupportsJson
    }

    /**
     * Return true if the media type is json.
     */
    private boolean isMediaTypeJson(String mediaType) {
        if (mediaType && (mediaType.equals('application/vnd.hedtech.integration+json') ||
                          mediaType.startsWith("application/vnd.hedtech+") ||
                          mediaType.startsWith("application/vnd.hedtech.v"))) {
            return false
        }
        switch (mediaType) {
            case ~/.*json$/:
                return true
                break
            default:
                return false
        }
    }

    /**
     * Return the list of http methods supported by the resource for a media type.
     */
    private List<String> findSupportedHttpMethods(ResourceDetail resourceDetail, String mediaType,Map representationMetadata) {
        List<String> httpMethods = []
        String httpMethod
        List<String> unsupportedMethods = resourceDetail.unsupportedMediaTypeMethods.get(mediaType)
        if (representationMetadata.get("qapiRequest")) {
            httpMethod = Methods.getHttpMethod("create")
            httpMethods.add(httpMethod.toLowerCase())
        }
        resourceDetail.methods.each() { method ->
            if (!unsupportedMethods?.contains(method)) {
                httpMethod = Methods.getHttpMethod(method)
                if (httpMethod) {
                    httpMethods.add(httpMethod.toLowerCase())
                }
            }
        }
        if(!(resourceDetail?.methods?.contains('show')) && representationMetadata.get("qapiRequest")){
            httpMethods.remove('get')
        }
        return httpMethods.unique().sort(customHttpMethodSorter)
    }

}
