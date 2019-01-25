/******************************************************************************
 Copyright 2017-2018 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.banner.restfulapi

import grails.transaction.Transactional
import net.hedtech.restfulapi.ContentExtensionResult
import net.hedtech.restfulapi.ContentExtensions
import org.springframework.transaction.annotation.Propagation

/**ApiErrorFactory
 * Created by sdorfmei on 5/19/17.
 */

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS )
class BannerContentExtensions implements ContentExtensions {

    def extensionProcessCompositeService

    /**
     * Apply extension to content.
     **/
    def ContentExtensionResult applyExtensions(String resourceName, def request, Map requestParams, def content, def isQapi) {

        ContentExtensionResult result = new ContentExtensionResult()
        result.extensionsApplied=false
        result.content = content

        //When we add the support for the version map this call needs to change....
        def ethosExtensionResult = extensionProcessCompositeService.applyExtensions(resourceName,
                request,
                requestParams,
                content,
                isQapi)

        if (ethosExtensionResult){
            result.extensionsApplied = ethosExtensionResult.extensionsApplied
            result.content  = ethosExtensionResult.content
        }


        return result
    }


}
