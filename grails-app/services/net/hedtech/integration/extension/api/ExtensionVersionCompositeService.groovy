/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension.api

import grails.gorm.transactions.Transactional
import net.hedtech.banner.exceptions.NotFoundException
import net.hedtech.integration.extension.ExtensionVersion
import net.hedtech.integration.utility.RestfulApiValidationUtility
import net.hedtech.integration.controller.ApiController
import net.hedtech.banner.exceptions.ApplicationException
import org.springframework.transaction.annotation.Propagation

class ExtensionVersionCompositeService extends ApiController {

    boolean transactional = true

    private static final int MAX_DEFAULT = RestfulApiValidationUtility.MAX_DEFAULT
    private static final int MAX_UPPER_LIMIT = RestfulApiValidationUtility.MAX_UPPER_LIMIT
    private static final String RESOURCE_NAME = 'extension-versions'

    def extensionVersionService

    /**
     * GET LIST
     *
     * @param params Request parameters
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    def list(Map params) {
        List<ExtensionVersion> resultList = extensionVersionService.list()
        return resultList
    }

    /**
     * The count method must return the total number of instances of the resource.
     * It is used in conjunction with the list method when returning a list of resources.
     * RestfulApiController will make call to "count" only if the "list" execution happens without any exception.
     *
     * @param params Request parameters
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    def count(Map params) {
        return extensionVersionService.count()
    }

    /**
     * Method to delete the passed in resource
     * @param params
     */
    void delete(Map params) {
        ExtensionVersion extensionVersion = extensionVersionService.getById(params?.id)
        if (extensionVersion){
            extensionVersionService.delete(extensionVersion.id)
        }else{
            throw new ApplicationException(RESOURCE_NAME, new NotFoundException())
        }
    }

    /**
     * Method for creating a new resource
     * @param content
     * @return
     */
    def create(Map content) {
        ExtensionVersion extensionVersion = new ExtensionVersion()
        bindData(extensionVersion,content,[:])
        return extensionVersionService.createOrUpdate(extensionVersion)
    }

    /**
     * Method for updating a resource
     * @param content
     * @return
     */
    def update(Map content) {
        ExtensionVersion extensionVersion = extensionVersionService.getById(content?.id)
        if (extensionVersion){
            bindData(extensionVersion, content, [:])
            extensionVersion =  extensionVersionService.createOrUpdate(extensionVersion)
        }else{
            throw new ApplicationException(RESOURCE_NAME, new NotFoundException())
        }

        return extensionVersion
    }

    /**
     * Get a single resource
     * @param id
     * @return
     */
    def get(def id) {
        ExtensionVersion extensionVersion = extensionVersionService.getById(id)
        if (!extensionVersion){
            throw new ApplicationException(RESOURCE_NAME, new NotFoundException())
        }
        return extensionVersion
    }
}
