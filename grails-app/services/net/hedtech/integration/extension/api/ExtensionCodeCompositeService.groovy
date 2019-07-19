/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension.api

import grails.gorm.transactions.Transactional
import net.hedtech.integration.controller.ApiController
import net.hedtech.integration.extension.ExtensionDefinitionCode
import net.hedtech.integration.utility.RestfulApiValidationUtility
import org.springframework.transaction.annotation.Propagation
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.NotFoundException

class ExtensionCodeCompositeService extends ApiController {

    boolean transactional = true

    private static final int MAX_DEFAULT = RestfulApiValidationUtility.MAX_DEFAULT
    private static final int MAX_UPPER_LIMIT = RestfulApiValidationUtility.MAX_UPPER_LIMIT

    private static final String RESOURCE_NAME = 'extension-codes'

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
        ExtensionDefinitionCode extensionDefinitionCode =  extensionDefinitionCodeService.getById(params?.id)
        if (extensionDefinitionCode){
            extensionDefinitionCodeService.delete(extensionDefinitionCode.id)
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
        ExtensionDefinitionCode extensionDefinitionCode = extensionDefinitionCodeService.getById(content?.id)
        if (extensionDefinitionCode){
            bindData(extensionDefinitionCode, content, [:])
            extensionDefinitionCode= extensionDefinitionCodeService.createOrUpdate(content)
        }else{
            throw new ApplicationException(RESOURCE_NAME, new NotFoundException())
        }

        return extensionDefinitionCode
    }

    /**
     * Get a single resource
     * @param id
     * @return
     */
    def get(def id) {
        ExtensionDefinitionCode extensionDefinitionCode = extensionDefinitionCodeService.getById(id)
        if (!extensionDefinitionCode){
            throw new ApplicationException(RESOURCE_NAME, new NotFoundException())
        }
        return extensionDefinitionCode
    }

}
