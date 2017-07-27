/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

/**
 * Class used to capture the read results for when the Ethos extension read process returns extensions
 */
class ExtensionProcessReadResult {

    String resourceId
    String jsonLabel
    String jsonPath
    String jsonPropertyType
    def value
}
