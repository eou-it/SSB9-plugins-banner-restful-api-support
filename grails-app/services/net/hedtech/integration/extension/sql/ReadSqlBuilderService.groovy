/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension.sql

import grails.transaction.Transactional
import net.hedtech.banner.service.ServiceBase
//import net.hedtech.banner.general.overall.SqlProcess

@Transactional
class ReadSqlBuilderService extends ServiceBase {

    boolean transactional = true

    /** Returns a list of SQL statements to aggregate and run **/
    List build(String sqlProcessCode, String sqlRuleCode){
        //def sqlStatements = SqlProcess.fetchSqlForExecutionByEntriesForSqlProcesssCodeAndEntriesForSqlCode(sqlProcessCode, sqlRuleCode)

        def sqlStatements = []
        String oneSql = "select gorguid_guid as guid, slbbldg_maximum_capacity from gorguid,slbbldg where gorguid_ldm_name = 'buildings' and gorguid_domain_surrogate_id = slbbldg_surrogate_id and gorguid_guid in (:GUID_LIST)"
        sqlStatements.add(oneSql)
        return sqlStatements
    }
}
