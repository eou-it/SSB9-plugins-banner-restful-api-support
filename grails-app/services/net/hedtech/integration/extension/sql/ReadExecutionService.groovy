/******************************************************************************
 Copyright 2017-2020 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/

package net.hedtech.integration.extension.sql

import org.hibernate.transform.AliasToEntityMapResultTransformer
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import net.hedtech.banner.service.ServiceBase

/**
 * Class used to execute the SQL to retrieve extended values
 */
@Transactional
import groovy.util.logging.Slf4j
@ Slf4j
class ReadExecutionService extends ServiceBase {

    /* Session factor used in the query call*/
    def sessionFactory

    /* Known parameter name for the list of GUIDs, this could be one or many */
    private static final String SQL_PARAM_GUID_LIST = "GUID_LIST"

    /**
     * Executes the passed in query SQL give the passed in list of GUIDs
     *
     * The GUID list should be one GUID or many.
     * @param selectSQL
     * @param guidList
     * @return
     */
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    def List execute(String selectSQL, def resourceIdList){
        if (log.isDebugEnabled()) log.debug "extension.sqlStatement=${selectSQL}"
        if (log.isTraceEnabled()) log.trace "extension.guidList=${resourceIdList}"

        def sqlQuery = sessionFactory.currentSession.createSQLQuery(selectSQL)
        sqlQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE)
        sqlQuery.setParameterList(SQL_PARAM_GUID_LIST, resourceIdList)
        def results = sqlQuery.list()

        if (log.isTraceEnabled()) log.trace "extension.results=${results}"
        return results
    }
}
