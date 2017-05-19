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

    /**Take the results and build process results**/
    def buildResults(def extensionDefinitions, def sqlResults){
        def extensionProcessReadResults = []
        if (sqlResults && extensionDefinitions){
            //For each sql query result row
            sqlResults.each { row ->
                    //For each column definition
                    extensionDefinitions.each { extensionDefinition->
                        ExtensionProcessReadResult extensionProcessReadResult = new ExtensionProcessReadResult()
                        extensionProcessReadResult.jsonLabel = extensionDefinition.jsonLabel
                        extensionProcessReadResult.jsonType = extensionDefinition.jsonType
                        extensionProcessReadResult.jsonPath = extensionDefinition.jsonPath
                        extensionProcessReadResult.resourceId = row.GUID
                        extensionProcessReadResult.value = row[extensionDefinition.selectColumnName]
                        extensionProcessReadResults.add(extensionProcessReadResult)
                    }
            }
        }

        return extensionProcessReadResults
    }
}
