/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension.api

import grails.transaction.Transactional
import net.hedtech.integration.controller.ApiController
import net.hedtech.integration.extension.ExtensionDefinitionCode
import net.hedtech.integration.utility.RestfulApiValidationUtility
import org.springframework.transaction.annotation.Propagation

class ExtensionCodeCompositeService extends ApiController {

    boolean transactional = true

    private static final int MAX_DEFAULT = RestfulApiValidationUtility.MAX_DEFAULT
    private static final int MAX_UPPER_LIMIT = RestfulApiValidationUtility.MAX_UPPER_LIMIT

    def extensionDefinitionCodeService

    /**
     * GET LIST
     *
     * @param params Request parameters
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    def list(Map params) {
        List<ExtensionDefinitionCode> resultList = extensionDefinitionCodeService.list()
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
        return extensionDefinitionCodeService.count()
    }


    /**
     * Method to delete the passed in resource
     * @param params
     */
    void delete(Map params) {
        extensionDefinitionCodeService.delete(params?.id)
    }

    /**
     * Method for creating a new resource
     * @param content
     * @return
     */
    def create(Map content) {
        ExtensionDefinitionCode extensionDefinitionCode = new ExtensionDefinitionCode()
        bindData(extensionDefinitionCode,content,[:])
        return extensionDefinitionCodeService.createOrUpdate(extensionDefinitionCode)
    }

    /**
     * Method for updating a resource
     * @param content
     * @return
     */
    def update(Map content) {
        String idInURI = content?.id
        ExtensionDefinitionCode extensionDefinitionCode = extensionDefinitionCodeService.getById(idInURI)
        if (extensionDefinitionCode){
            bindData(extensionDefinitionCode, content, [:])
            return extensionDefinitionCodeService.createOrUpdate(content)
        }
    }

    /**
     * Get a single resource
     * @param id
     * @return
     */
    def get(def id) {
        return extensionDefinitionCodeService.getById(id)
    }

}
