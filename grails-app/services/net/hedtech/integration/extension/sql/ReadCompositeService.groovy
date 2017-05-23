/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension.sql

import net.hedtech.banner.service.ServiceBase

/**
 * Service to read the data sources for extensions and build a result
 */
class ReadCompositeService extends ServiceBase {
    def readSqlBuilderService
    def readResultBuilderService
    def readExecutionService
    def extensionDefinitionSourceGroupBuilderService

    /**
     * Reads the extensions and returns them as process results
     * @param extensionDefinitionList
     * @param resourceIdList
     * @return
     */
    def read(def extensionDefinitionList, def resourceIdList){
        def processResults = []

        //For each data source of the extensions
        def extensionDefinitionGroupList = extensionDefinitionSourceGroupBuilderService.build(extensionDefinitionList)
        if (extensionDefinitionGroupList && resourceIdList){
            extensionDefinitionGroupList.each { extensionDefinitionGroup ->
                def sqlStatements = readSqlBuilderService.build(extensionDefinitionGroup)
                if (sqlStatements){
                    sqlStatements.each { sqlStatement ->
                        def executeResults = readExecutionService.execute(sqlStatement,resourceIdList)
                        def executeProcessResults = readResultBuilderService.buildResults(extensionDefinitionGroup.extensionDefinitionList,executeResults)
                        if (executeProcessResults){
                            processResults.addAll(executeProcessResults)
                        }
                    }
                }else{
                    log.warn "There where no SQL Statements to run."
                }
            }
        }else{
            log.warn "A read action was called when there are no extensions. This is unexpected."
        }

       return processResults
    }

}
