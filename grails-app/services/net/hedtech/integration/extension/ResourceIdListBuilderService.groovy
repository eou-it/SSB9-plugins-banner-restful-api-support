/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

import net.hedtech.banner.service.ServiceBase


/**
 * Created by sdorfmei on 5/16/17.
 */
class ResourceIdListBuilderService extends ServiceBase {

    def buildFromCollection(def content){

        //For the passed in content pull out a list of guids.

        //If one (not an array) return the one guid, else return the list
        def guidList = ['24c47f0a-0eb7-48a3-85a6-2c585691c6ce','26a2673f-9bc6-4649-a3e8-213d0ff4afbd','acc93569-9275-47d4-986a-313f52ff8044']

        return guidList
    }
}
