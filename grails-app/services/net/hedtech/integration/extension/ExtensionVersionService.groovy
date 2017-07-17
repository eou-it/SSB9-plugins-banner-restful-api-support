/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import grails.transaction.Transactional
import net.hedtech.banner.service.ServiceBase

@Transactional
class ExtensionVersionService extends ServiceBase {

    boolean transactional = true

    /**
     * Find by the resource name and known version
     * @param resourceName
     * @param known
     * @return
     */
    def findByResourceNameAndKnownMediaType(String resourceName, String knownMediaType){
        def extensionVersionResult = ExtensionVersion.fetchByResourceNameAndKnownMediaType(resourceName,knownMediaType)
        return extensionVersionResult
    }


}
