package net.hedtech.banner.restfulapi

import grails.transaction.Transactional
import net.hedtech.restfulapi.ContentExtensionResult
import net.hedtech.restfulapi.ContentExtensions
import org.springframework.transaction.annotation.Propagation

/**
 * Created by sdorfmei on 5/19/17.
 */
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS )
class BannerContentExtensions implements ContentExtensions {

    def extensionProcessCompositeService
    private static final String RESPONSE_REPRESENTATION = 'net.hedtech.restfulapi.RestfulApiController.response_representation'

    /**
     * Apply extension to content.
     **/
    def ContentExtensionResult applyExtensions(String resourceName, def request, Map requestParams, def content) {

        ContentExtensionResult result = new ContentExtensionResult()
        result.wasExtended=false
        result.content = content

        def representationConfig = request.getAttribute(RESPONSE_REPRESENTATION)

        //When we add the support for the version map this call needs to change....
        def ethosExtensionResult = extensionProcessCompositeService.applyExtensions(resourceName,
                representationConfig.mediaType,
                request,
                requestParams,
                content)

        if (ethosExtensionResult){
            result.wasExtended = ethosExtensionResult.wasExtended
            result.content  = ethosExtensionResult.content
        }


        return result
    }


}
