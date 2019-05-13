/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension.api

import grails.gorm.transactions.Transactional
import net.hedtech.banner.exceptions.NotFoundException
import net.hedtech.integration.extension.ExtensionDefinition
import net.hedtech.integration.utility.RestfulApiValidationUtility
import org.springframework.transaction.annotation.Propagation
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.integration.controller.ApiController

class ExtensionDefinitionCompositeService extends ApiController {

    boolean transactional = true

    private static final int MAX_DEFAULT = RestfulApiValidationUtility.MAX_DEFAULT
    private static final int MAX_UPPER_LIMIT = RestfulApiValidationUtility.MAX_UPPER_LIMIT

    private static final String RESOURCE_NAME = 'extension-definitions'

    def extensionDefinitionService
    /**
     * GET LIST
     *
     * @param params Request parameters
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    def list(Map params) {
        List<ExtensionDefinition> resultList = extensionDefinitionService.list()
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
        return extensionDefinitionService.count()
    }


    /**
     * Method to delete the passed in resource
     * @param params
     */
    void delete(Map params) {
        ExtensionDefinition extensionDefinition = extensionDefinitionService.getById(params?.id)
        if (extensionDefinition) {
            extensionDefinitionService.delete(extensionDefinition.id)
        }
        else{
            throw new ApplicationException(RESOURCE_NAME, new NotFoundException())
        }
    }

    /**
     * Method for creating a new resource
     * @param content
     * @return
     */
    def create(Map content) {
        ExtensionDefinition extensionDefinition = new ExtensionDefinition()
        bindData(extensionDefinition,content,[:])
        return extensionDefinitionService.createOrUpdate(extensionDefinition)
    }

    /**
     * Method for updating a resource
     * @param content
     * @return
     */
    def update(Map content) {
        ExtensionDefinition extensionDefinition = extensionDefinitionService.getById(content?.id)
        if (extensionDefinition){
            bindData(extensionDefinition, content, [:])
            extensionDefinition = extensionDefinitionService.createOrUpdate(extensionDefinition)
        }else{
            throw new ApplicationException(RESOURCE_NAME, new NotFoundException())
        }
        return extensionDefinition
    }

    /**
     * Get a single resource
     * @param id
     * @return
     */
    def get(def id) {
        ExtensionDefinition extensionDefinition = extensionDefinitionService.getById(id)
        if (!extensionDefinition){
            throw new ApplicationException(RESOURCE_NAME, new NotFoundException())
        }
        return extensionDefinition
    }
}
