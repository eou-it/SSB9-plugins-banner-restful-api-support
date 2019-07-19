/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension.sql

import grails.gorm.transactions.Transactional
import net.hedtech.banner.service.ServiceBase
import net.hedtech.integration.extension.ExtensionDefinitionSourceGroup
import org.springframework.transaction.annotation.Propagation

@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
class ReadSqlBuilderService extends ServiceBase {
    boolean transactional = true

    /**
     * Returns a list of SQL Statements to execute to read extensions
     * @param extensionDefinitionSourceGroup
     * @return
     */
    List build(ExtensionDefinitionSourceGroup extensionDefinitionSourceGroup){
        def sqlStatements
        if (extensionDefinitionSourceGroup){
            //Only GORSQL code is supported today, but future could derive SQL, thus the check
            if (extensionDefinitionSourceGroup.sqlProcessCode && extensionDefinitionSourceGroup.sqlRuleCode){
                sqlStatements = ApiSqlProcess.fetchSqlForExecutionSqlProcesssCodeAndRuleCode(
                        extensionDefinitionSourceGroup.sqlProcessCode,
                        extensionDefinitionSourceGroup.sqlRuleCode)
            }else{
                log.debug "There is no sql process and/or rule code defined for the extension definition source group"
            }
        }
        return sqlStatements
    }
}
