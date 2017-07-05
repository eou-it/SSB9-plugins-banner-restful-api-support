/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import grails.transaction.Transactional
import net.hedtech.banner.service.ServiceBase

@Transactional
class ExtensionDefinitionService extends ServiceBase {

    boolean transactional = true

    /**
     * Return all the Extension Definitions for the requested resource name and extension code
     * @param resourceName
     * @param catalog
     * @return
     */
    def findAllByResourceNameAndExtensionCode(String resourceName, String extensionCode){
        List<ExtensionDefinition> extensionDefinitionList = ExtensionDefinition.fetchAllByResourceNameAndExtensionCode(resourceName,extensionCode)
        return extensionDefinitionList
    }

}
