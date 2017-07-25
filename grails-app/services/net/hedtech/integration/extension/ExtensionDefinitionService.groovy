/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import grails.transaction.Transactional
import net.hedtech.banner.service.ServiceBase
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.NotFoundException

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

    /**
     * Returns a count of the resources
     * @return
     */
    def count(){
        return ExtensionDefinition.countAll()
    }

    /**
     * Returns all the resources
     * @return
     */
    def list(){
        return ExtensionDefinition.fetchAll()
    }


    /**
     * Returns a given resource by id
     * @param id
     * @return
     */
    def getById(def id) {
        return ExtensionDefinition.findById(id)
    }

    def delete(def id){

        ExtensionDefinition extensionDefinition = getById(id)
        if (extensionDefinition){
            extensionDefinition.delete(flush: true, failOnError: true)
        }else{
            throw new ApplicationException("extension-definitions", new NotFoundException())
        }
        return extensionDefinitionCode
    }

}
