package net.hedtech.banner.restfulapi

import net.hedtech.integration.resource.RepresentationResolutionService
import net.hedtech.restfulapi.RepresentationResolver
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * Created by sdorfmei on 6/20/17.
 */
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS )
class BannerRepresentationResolver implements RepresentationResolver {

    RepresentationResolutionService representationResolutionService

    String getResponseRepresentationString(def pluralizedResourceName,def request){
        return representationResolutionService.getResponseRepresentationString(pluralizedResourceName,request)
    }

    String getRequestRepresentationMediaType(def pluralizedResourceName,def request){
        return representationResolutionService.getRequestRepresentationMediaType(pluralizedResourceName,request)
    }

    String getResponseRepresentationMediaType(def pluralizedResourceName,def request){
        return representationResolutionService.getResponseRepresentationMediaType(pluralizedResourceName,request)
    }

}
