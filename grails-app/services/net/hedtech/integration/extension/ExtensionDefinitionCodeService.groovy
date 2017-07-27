/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import grails.transaction.Transactional
import net.hedtech.banner.service.ServiceBase

@Transactional
class ExtensionDefinitionCodeService extends ServiceBase {

    boolean transactional = true

    /**
     * Returns a count of the resources
     * @return
     */
    def count(){
        return ExtensionDefinitionCode.countAll()
    }

    /**
     * Returns all the resources
     * @return
     */
    def list(){
        return ExtensionDefinitionCode.fetchAll()
    }

    /**
     * Returns a given resource by id
     * @param id
     * @return
     */
    def getById(def id) {
        return ExtensionDefinitionCode.findById(id)
    }

    /**
     * Delete a resource by ID
     * @param id
     * @return
     */
    def delete(def id){
        ExtensionDefinitionCode extensionDefinitionCode = getById(id)
        if (extensionDefinitionCode){
            extensionDefinitionCode.delete(flush: true, failOnError: true)
        }
        return extensionDefinitionCode
    }
}
