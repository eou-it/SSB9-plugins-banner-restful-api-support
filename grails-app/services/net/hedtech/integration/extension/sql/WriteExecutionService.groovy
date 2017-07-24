/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension.sql

import net.hedtech.integration.extension.ExtensionDefinition
import net.hedtech.integration.extension.ExtractedExtensionPropertyGroup
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

    /* Known parameter name for the HTTP method */
    private static final String SQL_PARAM_HTTPMETHOD = "HTTP_METHOD"

    //Banner unspecified values
    private static final char SQL_UNSPECIFIED_STRING = (char)1
    private static final long SQL_UNSPECIFIED_NUMBER = 1E-35
    private static final Date SQL_UNSPECIFIED_DATE = new Date(1000,01,01)
    /**
     * Executes the passed in query SQL give the passed in GUID
     *
     * @param updateSql
     * @param guid
     * @return
     */
    @Transactional(readOnly=false, propagation=Propagation.REQUIRED)
    def void execute(String writeSql, def resourceId, def httpMethod, ExtractedExtensionPropertyGroup extractedExtensionPropertyGroup){
        if (log.isDebugEnabled()) log.debug "extension.sqlStatement=${writeSql}"
        if (log.isTraceEnabled()) log.trace "extension.guid=${resourceId}"

        def sqlQuery = sessionFactory.currentSession.createSQLQuery(writeSql)

        //Set hard wired / expected parameters
        sqlQuery.setString(SQL_PARAM_GUID, resourceId)
        sqlQuery.setString(SQL_PARAM_HTTPMETHOD, httpMethod)


        def extractedExtensionPropertyList = extractedExtensionPropertyGroup.extractedExtensionPropertyList
        extractedExtensionPropertyList.each { extractedExtensionProperty ->
            ExtensionDefinition extensionDefinition = extractedExtensionProperty.extendedDefinition
            if (extensionDefinition) {
                def parameterType = extensionDefinition.jsonPropertyType
                def parameterName = extensionDefinition.columnName
                def parameterValue = extractedExtensionProperty.value

                if (parameterType=="S") {
                   if (extractedExtensionProperty.valueWasMissing){
                       parameterValue = SQL_UNSPECIFIED_STRING
                       sqlQuery.setCharacter(parameterName, parameterValue)
                   }else{
                       sqlQuery.setString(parameterName, parameterValue)
                   }

                } else if (parameterType=="N") {
                    if (extractedExtensionProperty.valueWasMissing){
                        parameterValue = SQL_UNSPECIFIED_NUMBER
                    }
                    sqlQuery.setBigDecimal(parameterName, parameterValue)
                } else if (parameterType=="D") {
                    if (extractedExtensionProperty.valueWasMissing){
                        parameterValue = SQL_UNSPECIFIED_DATE
                    }
                    sqlQuery.setDate(parameterName, parameterValue)
                } else if (parameterType=="C") {
                    if (extractedExtensionProperty.valueWasMissing){
                        parameterValue = SQL_UNSPECIFIED_STRING
                    }
                    sqlQuery.setCharacter(parameterName, parameterValue)
                }
            }
        }

        sqlQuery.executeUpdate()

        if (log.isTraceEnabled()) log.trace "extension.writeSuccessful"
    }
}
