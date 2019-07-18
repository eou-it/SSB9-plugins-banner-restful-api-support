/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.resource

import net.hedtech.integration.extension.ExtensionVersion
import net.hedtech.integration.extension.ExtensionVersionService
import grails.web.http.HttpHeaders
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import grails.gorm.transactions.Transactional

/**
 * Created by sdorfmei on 6/21/17.
 */
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS )
public class RepresentationResolutionService {

    private static final String catalogHeaderName = "X-Catalog"

    ExtensionVersionService extensionVersionService

    String getCatalogResponseHeaderName(){
        return (catalogHeaderName)
    }

    String getRequestRepresentationMediaType(def pluralizedResourceName,def request){
        return (getRepresentationFromHeader(false,HttpHeaders.CONTENT_TYPE, pluralizedResourceName, request))
    }

    String getResponseRepresentationMediaType(def pluralizedResourceName,def request){
        return (getRepresentationFromHeader(false,HttpHeaders.ACCEPT, pluralizedResourceName, request))
    }


    String getRequestCatalog(def request){
        return request.getHeader(catalogHeaderName)
    }

    private String getRepresentationFromHeader(boolean translate, def header, def pluralizedResourceName,def request){
        String representation
        if (request){
            String requestHeaderValue = request.getHeader(header)
            String responseCatalog = request.getHeader(catalogHeaderName)
            if (responseCatalog){
                if (extensionVersionService){
                    ExtensionVersion extensionVersion = extensionVersionService.findByResourceName(pluralizedResourceName)
                    if (extensionVersion){
                            representation=extensionVersion.knownMediaType

                    }else{
                        representation=responseCatalog
                    }

                }else{
                    representation=responseCatalog
                }

                if (representation && translate){
                    representation=translateCatalogToMediaType(responseCatalog)
                }
            }else{
                representation=requestHeaderValue
            }
        }

        return (representation)
    }

    private String translateCatalogToMediaType(String catalog){
        return("application/vnd.hedtech.catalog." + catalog + "+json");
    }


}
