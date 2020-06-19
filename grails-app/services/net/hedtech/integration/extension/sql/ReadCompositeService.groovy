/******************************************************************************
 Copyright 2017-2020 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension.sql

import net.hedtech.banner.service.ServiceBase

/**
 * Service to read the data sources for extensions and build a set of pojo results
 */
import groovy.util.logging.Slf4j
@ Slf4j
class ReadCompositeService extends ServiceBase {

    def readSqlBuilderService
    def readResultBuilderService
    def readExecutionService

    /**
     * Reads the extensions and returns them as process results
     * @param extensionDefinitionList
     * @param resourceIdList
     * @return
     */
    def read(def extensionDefinitionGroupList, def resourceIdList){
        def processResults = []

        //Stopwatch start
        def startTime = new Date()

        //For each data source of the extension
        if (extensionDefinitionGroupList && resourceIdList){
            extensionDefinitionGroupList.each { extensionDefinitionGroup ->
                //Get a list of sql statements from GORRSQL, note there can be many
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
        //Stopwatch stop
        def endTime = new Date()

        log.debug("Ethos Extensions read from db and build results time ms: ${endTime.time - startTime.time}")
        return processResults
    }

}
