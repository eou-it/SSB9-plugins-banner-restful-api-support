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
     * Return all the Extension Definitions for the requested resource name and type
     * @param resourceName
     * @param catalog
     * @return
     */
    def findByResourceNameAndCatalog(String resourceName, String catalog){
        return ExtensionDefinition.findAllByResourceNameAndResourceCatalog(resourceName,catalog)
    }
}
