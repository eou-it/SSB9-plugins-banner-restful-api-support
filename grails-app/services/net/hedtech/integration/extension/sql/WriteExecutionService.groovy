/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension.sql

import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import net.hedtech.banner.service.ServiceBase

/**
 * Class used to execute the SQL to write extended values
 */
@Transactional
class WriteExecutionService extends ServiceBase {

    /* Session factor used in the query call*/
    def sessionFactory

    /* Known parameter name for the GUID */
    private static final String SQL_PARAM_GUID = "GUID"

    /**
     * Executes the passed in query SQL give the passed in GUID
     *
     * @param updateSql
     * @param guid
     * @return
     */
    @Transactional(readOnly=false, propagation=Propagation.REQUIRED)
    def void execute(String updateSql, def resourceId, Map parameterList){
        if (log.isDebugEnabled()) log.debug "extension.sqlStatement=${updateSql}"
        if (log.isTraceEnabled()) log.trace "extension.guid=${resourceId}"

        def sqlQuery = sessionFactory.currentSession.createSQLQuery(updateSql)
        sqlQuery.setString(SQL_PARAM_GUID, resourceId)
        parameterList.each { parameter ->
            String parameterName = parameter.key
            def parameterValue = parameter.value
            if (parameterValue instanceof String) {
                sqlQuery.setString(parameterName, parameterValue)
            } else if (parameterValue instanceof Number) {
                sqlQuery.setBigDecimal(parameterName, parameterValue)
            } else if (parameterValue instanceof Date) {
                sqlQuery.setDate(parameterName, parameterValue)
            } else if (parameterValue instanceof Character) {
                sqlQuery.setCharacter(parameterName, parameterValue)
            } else if (parameterValue == null) {
                sqlQuery.setNull(parameterName)
            }
        }
        sqlQuery.executeUpdate()

        if (log.isTraceEnabled()) log.trace "extension.updateSuccessful"
    }
}
