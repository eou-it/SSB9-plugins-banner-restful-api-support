/******************************************************************************
 Copyright 2017-2020 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import grails.gorm.transactions.Transactional
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.NotFoundException
import net.hedtech.banner.service.ServiceBase

@Transactional
class ExtensionVersionService extends ServiceBase {

    boolean transactional = true

    /**
     * Find by the resource name and known version
     * @param resourceName
     * @param knownMediaType
     * @return
     */
    def findByResourceNameAndKnownMediaType(String resourceName, String knownMediaType){
        def extensionVersionResult = ExtensionVersion.fetchByResourceNameAndKnownMediaType(resourceName,knownMediaType)
        return extensionVersionResult
    }

    /**
     * Returns a count of the resources
     * @return
     */
    def count(){
        return ExtensionVersion.countAll()
    }

    /**
     * Returns all the resources
     * @return
     */
    def list(){
        return ExtensionVersion.fetchAll()
    }

    /**
     * Returns a given resource by id
     * @param id
     * @return
     */
    def getById(def id) {
        return ExtensionVersion.findById(id)
    }

    def delete(def id){

        ExtensionVersion extensionVersion = getById(id)
        if (extensionVersion){
            extensionVersion.delete(flush: true, failOnError: true)
        }else{
            throw new ApplicationException("extension-versions", new NotFoundException())
        }
        return extensionVersion
    }

    /**
     * Find all by the resource name
     * @param resourceName
     * @return
     */
    List<ExtensionVersion> fetchAllByResourceName(String resourceName) {
        List<ExtensionVersion> extensionVersionList = []
        if (resourceName) {
            extensionVersionList = ExtensionVersion.fetchAllByResourceName(resourceName)
        }

        return extensionVersionList
    }

}
