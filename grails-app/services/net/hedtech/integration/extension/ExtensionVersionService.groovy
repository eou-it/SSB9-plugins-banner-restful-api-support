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
     * Return all the Extension Versions
     * @param resourceName
     * @param catalog
     * @return
     */
    //def findAllByResourceNameAndExtensionCode(String resourceName, String extensionCode){
     //   return ExtensionVersion.findAllByResourceNameAndExtensionCode(resourceName,extensionCode)
   // }
}
