/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension.sql

import net.hedtech.banner.service.ServiceBase
import net.hedtech.integration.extension.ExtensionDefinitionGroup

/**
 * Service to read the data sources for extensions and build a result
 */
class ReadCompositeService extends ServiceBase {
    def readSqlBuilderService
    def readResultBuilderService
    def readExecutionService
    def resourceIdListBuilderService
    def extensionDefinitionGroupBuilderService


    def read(def extensionDefinitionList, def responseContent){
        def processResults = []
        //Get a list of GUIDs from the response
        //This could be a single GUID (from a GET by GUID or many GET list)
        def guidList = resourceIdListBuilderService.buildFromCollection(responseContent)

        //For each group of defintions
        def extensionDefinitionGroupList = extensionDefinitionGroupBuilderService.build(extensionDefinitionList)
        if (extensionDefinitionGroupList){
            extensionDefinitionGroupList.each { extensionDefinitionGroup ->
                def sqlStatements = readSqlBuilderService.build(extensionDefinitionGroup.sqlProcesCode, extensionDefinitionGroup.sqlRuleCode)
                if (sqlStatements){
                    sqlStatements.each { sqlStatement ->
                        def executeResults = readExecutionService.execute(sqlStatement,guidList)
                        def executeProcessResults = readResultBuilderService.buildResults(extensionDefinitionGroup.extensionDefinitionList,executeResults)
                        if (executeProcessResults){
                            processResults.addAll(executeProcessResults)
                        }
                    }
                }
            }
        }

       return processResults
    }

}
