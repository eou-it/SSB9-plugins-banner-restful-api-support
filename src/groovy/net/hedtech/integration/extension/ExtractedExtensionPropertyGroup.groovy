/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

/**
 * Class used to group extracted values from requests together
 */
class ExtractedExtensionPropertyGroup {

    String sqlProcessCode
    String sqlRuleCode

    List<ExtractedExtensionProperty> extractedExtensionPropertyList

    /*
       Helper function to transform the columns in each definition into a key/value map
     */
    Map buildParameterMap(){
        Map resultMap = null
        if (extractedExtensionPropertyList) {
            resultMap =[:]
            extractedExtensionPropertyList.each { extractedExtensionProperty ->
                ExtensionDefinition extensionDefinition = extractedExtensionProperty.extendedDefinition
                if (extensionDefinition) {
                    //That select column name should be update
                    resultMap.put(extensionDefinition.columnName,extractedExtensionProperty.value)
                }
            }
        }
        return resultMap
    }
}

