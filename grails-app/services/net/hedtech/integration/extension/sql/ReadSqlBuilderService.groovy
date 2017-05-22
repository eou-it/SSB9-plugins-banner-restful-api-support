/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension.sql

import grails.transaction.Transactional
import net.hedtech.banner.service.ServiceBase
import net.hedtech.integration.extension.ExtensionDefinitionSourceGroup
import org.springframework.transaction.annotation.Propagation

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
class ReadSqlBuilderService extends ServiceBase {
    boolean transactional = true
    /** Returns a list of SQL statements to aggregate and run **/
    List build(ExtensionDefinitionSourceGroup extensionDefinitionSourceGroup){
        def sqlStatements
        if (extensionDefinitionSourceGroup){
            //Only GORSQL code is supported today, but future could derive SQL, thus the check
            if (extensionDefinitionSourceGroup.sqlProcesCode && extensionDefinitionSourceGroup.sqlRuleCode){
                sqlStatements = ApiSqlProcess.fetchSqlForExecutionSqlProcesssCodeAndRuleCode(
                        extensionDefinitionSourceGroup.sqlProcesCode,
                        extensionDefinitionSourceGroup.sqlRuleCode)
            }else{
                log.debug "There is no sql process and/or rule code defined for the extension definition source group"
            }
        }
        return sqlStatements
    }
}
