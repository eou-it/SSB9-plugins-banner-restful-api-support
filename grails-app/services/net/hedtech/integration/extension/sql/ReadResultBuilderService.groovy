/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension.sql

import net.hedtech.banner.service.ServiceBase
import net.hedtech.integration.extension.ExtensionProcessReadResult

/**
 * Service that transforms a direct database recordset result to a domain process result for use
 */
class ReadResultBuilderService extends ServiceBase {

    /**
     * Take the list of extension definitions and the sql results and build a list of process results
     * @param extensionDefinitions
     * @param sqlResults
     * @return
     */
    def buildResults(def extensionDefinitions, def sqlResults){
        def extensionProcessReadResults = []
        if (sqlResults && extensionDefinitions){
            //For each sql query result row
            sqlResults.each { row ->
                //For each column definition
                extensionDefinitions.each { extensionDefinition->
                    ExtensionProcessReadResult extensionProcessReadResult = new ExtensionProcessReadResult()
                    extensionProcessReadResult.jsonLabel = extensionDefinition.jsonLabel
                    extensionProcessReadResult.jsonPropertyType = extensionDefinition.jsonPropertyType
                    extensionProcessReadResult.jsonPath = extensionDefinition.jsonPath

                    if (row[extensionDefinition.columnName]){
                        extensionProcessReadResult.resourceId = row.GUID
                        extensionProcessReadResult.value = row[extensionDefinition.columnName]

                        extensionProcessReadResults.add(extensionProcessReadResult)
                    }

                }
            }
        }
        return extensionProcessReadResults
    }
}
