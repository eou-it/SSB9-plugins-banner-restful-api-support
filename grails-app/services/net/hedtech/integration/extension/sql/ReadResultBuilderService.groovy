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

                        //Check the jsonType if its a property make sure we have a value from the db
                        String jsonType = extensionProcessReadResult.jsonType
                        if (jsonType && jsonType.equalsIgnoreCase("property")){
                            if (row[extensionDefinition.selectColumnName]){
                                extensionProcessReadResult.resourceId = row.GUID
                                extensionProcessReadResult.value = row[extensionDefinition.selectColumnName]

                                extensionProcessReadResults.add(extensionProcessReadResult)
                            }
                        }else{
                            //Its not a property (thus no value from the db just add it
                            extensionProcessReadResults.add(extensionProcessReadResult)
                        }

                    }
            }
        }

        return extensionProcessReadResults
    }
}
