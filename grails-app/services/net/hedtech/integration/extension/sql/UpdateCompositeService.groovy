/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension.sql

import net.hedtech.banner.service.ServiceBase

/**
 * Created by sdorfmei on 7/14/17.
 */
class UpdateCompositeService extends ServiceBase {

    def writeSqlBuilderService

    def writeExecutionService

    def update(def resourceId, def extractedExtensionPropertyGroupList) {

        //Stopwatch start
        def startTime = new Date()

        //For each data source of the extension
        if (extractedExtensionPropertyGroupList && resourceId){
            extractedExtensionPropertyGroupList.each { extractedExtensionPropertyGroup ->

                //Get a list of sql statements from GORRSQL, note there can be many
                def sqlStatements = writeSqlBuilderService.build(extractedExtensionPropertyGroup)
                if (sqlStatements){
                    sqlStatements.each { sqlStatement ->
                        def executeResults = writeExecutionService.execute(sqlStatement,resourceId,extractedExtensionPropertyGroup.buildParameterMap())
                        //Handle results?
                    }
                }else{
                    log.warn "There where no SQL Statements to run."
                }
            }
        }else{
            log.warn "A update/save action was called when there are no extensions. This is unexpected."
        }
        //Stopwatch stop
        def endTime = new Date()

        log.debug("Ethos Extensions save from db and build results time ms: ${endTime.time - startTime.time}")
       // return processResults
    }
}
