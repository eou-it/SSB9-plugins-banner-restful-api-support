/******************************************************************************
 Copyright 2017-2020 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension.sql

import net.hedtech.banner.service.ServiceBase
import org.springframework.transaction.annotation.Transactional

@Transactional
import groovy.util.logging.Slf4j
@ Slf4j
class WriteCompositeService extends ServiceBase {

    def writeSqlBuilderService

    def writeExecutionService

    /**
     * Update method to apply update from the extension values found
     * @param resourceId
     * @param extractedExtensionPropertyGroupList
     * @return
     */
    def write(def resourceId, def httpMethod, def extractedExtensionPropertyGroupList) {

        //Stopwatch start
        def startTime = new Date()

        //For each data source of the extension
        if (extractedExtensionPropertyGroupList && resourceId){
            extractedExtensionPropertyGroupList.each { extractedExtensionPropertyGroup ->

                //Get a list of sql statements from GORRSQL, note there can be many
                def sqlStatements = writeSqlBuilderService.build(extractedExtensionPropertyGroup)
                if (sqlStatements){
                    sqlStatements.each { sqlStatement ->
                        writeExecutionService.execute(sqlStatement,resourceId,httpMethod,extractedExtensionPropertyGroup)
                    }
                }else{
                    log.warn "There where no SQL Statements to run."
                }
            }
        }else{
            log.warn "A write action was called when there are no extensions. This is unexpected."
        }
        //Stopwatch stop
        def endTime = new Date()

        log.debug("Ethos Extensions write from db and build results time ms: ${endTime.time - startTime.time}")
        // return processResults
    }
}
