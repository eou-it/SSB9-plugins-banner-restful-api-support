/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension.sql
import grails.transaction.Transactional
import net.hedtech.banner.service.ServiceBase
import net.hedtech.integration.extension.ExtensionDefinitionSourceGroup
import net.hedtech.integration.extension.ExtractedExtensionPropertyGroup
import org.springframework.transaction.annotation.Propagation


/**
 * Created by sdorfmei on 7/17/17.
 */
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
class WriteSqlBuilderService  extends ServiceBase {
    boolean transactional = true

    /**
     * Returns a list of SQL Statements to execute to update extensions
     * @param extractedExtensionPropertyGroup
     * @return
     */
    List build(ExtractedExtensionPropertyGroup extractedExtensionPropertyGroup){
        def sqlStatements
        if (extractedExtensionPropertyGroup){
            //Only GORSQL code is supported today, but future could derive SQL, thus the check
            if (extractedExtensionPropertyGroup.sqlProcessCode && extractedExtensionPropertyGroup.sqlRuleCode){
                sqlStatements = ApiSqlProcess.fetchSqlForExecutionSqlProcesssCodeAndRuleCode(
                        extractedExtensionPropertyGroup.sqlProcessCode,
                        extractedExtensionPropertyGroup.sqlRuleCode)
            }else{
                log.debug "There is no sql process and/or rule code defined for the extracted extension property group"
            }
        }
        return sqlStatements
    }
}
