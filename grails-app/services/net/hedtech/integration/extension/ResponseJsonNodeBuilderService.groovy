/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Class that builds a root JSON node from a http request
 */
class ResponseJsonNodeBuilderService {

    /**
     * Function to read the tree of the request body and put in a JSON node
     * @param responseContent
     * @return
     */
    def build(def responseContent){
        JsonNode rootNode = null
        if (responseContent){
            def ObjectMapper MAPPER = new ObjectMapper();
            rootNode = MAPPER.readTree(responseContent);
        }
        return rootNode
    }

}
