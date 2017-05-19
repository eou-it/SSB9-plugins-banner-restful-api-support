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
    def resourceIdListBuilderService
    def extensionDefinitionSourceGroupBuilderService


    def read(def extensionDefinitionList, Map requestParms, def responseContent){
        def processResults = []
        //Get a list of GUIDs from the response
        //This could be a single GUID (from a GET by GUID or many GET list)
        //
        //To do...really should not need the requestParams...just parse contents better
        def guidList = null
        if (requestParms && requestParms.id){
            guidList = resourceIdListBuilderService.buildFromGuid(requestParms.id)
        }else{
            guidList = resourceIdListBuilderService.buildFromContentList(responseContent)
        }

        //For each data source of the extensions
        def extensionDefinitionGroupList = extensionDefinitionSourceGroupBuilderService.build(extensionDefinitionList)
        if (extensionDefinitionGroupList){
            extensionDefinitionGroupList.each { extensionDefinitionGroup ->
                def sqlStatements = readSqlBuilderService.build(extensionDefinitionGroup)
                if (sqlStatements){
                    sqlStatements.each { sqlStatement ->
                        def executeResults = readExecutionService.execute(sqlStatement,guidList)
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
