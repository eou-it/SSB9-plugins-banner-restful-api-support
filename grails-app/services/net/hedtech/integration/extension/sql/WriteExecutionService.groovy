/******************************************************************************
 Copyright 2017-2020 Ellucian Company L.P. and its affiliates.
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
import groovy.util.logging.Slf4j
@ Slf4j
class WriteExecutionService extends ServiceBase {

    /* Session factor used in the query call*/
    def sessionFactory

    /* Known parameter name for the GUID */
    private static final String SQL_PARAM_GUID = "GUID"

    /* Known parameter name for the HTTP method */
    private static final String SQL_PARAM_HTTPMETHOD = "HTTP_METHOD"

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

        // added the below line for integration tests
        writeSql = writeSql.replaceAll(":=", "\\\\:=")    // must escape the ':=' with '\:=' to allow for hibernate binding
        def sqlQuery = sessionFactory.currentSession.createSQLQuery(writeSql)

        //Set hard wired / expected parameters
        sqlQuery.setString(SQL_PARAM_GUID, resourceId)
        sqlQuery.setString(SQL_PARAM_HTTPMETHOD, httpMethod)

        def extractedExtensionPropertyList = extractedExtensionPropertyGroup.extractedExtensionPropertyList
        extractedExtensionPropertyList.each { extractedExtensionProperty ->
            ExtensionDefinition extensionDefinition = extractedExtensionProperty.extensionDefinition
            if (extensionDefinition) {
                def parameterName = extensionDefinition.columnName
                def jsonPropertyType = extensionDefinition.jsonPropertyType
                def parameterValue = extractedExtensionProperty.value

                // verify parameter name is referenced in the query
                if (writeSql.contains(":${parameterName?.toUpperCase()}")) {

                    //If there is a value, set the correct parameter type
                    if (parameterValue instanceof String) {
                        sqlQuery.setString(parameterName, parameterValue)
                    } else if (parameterValue instanceof Number) {
                        sqlQuery.setBigDecimal(parameterName, parameterValue)
                    } else if (parameterValue instanceof Date) {
                        sqlQuery.setDate(parameterName, parameterValue)
                    } else if (parameterValue instanceof Character) {
                        sqlQuery.setCharacter(parameterName, parameterValue)
                    } else if (parameterValue == null) {

                        //If there is not a value figure out how to pass an unspecified value
                        if (jsonPropertyType == "S") {
                            if (extractedExtensionProperty.valueWasMissing) {
                                parameterValue = BannerSqlConstants.UNSPECIFIED_STRING
                            }
                            sqlQuery.setCharacter(parameterName, parameterValue)
                        } else if (jsonPropertyType == "N") {
                            if (extractedExtensionProperty.valueWasMissing) {
                                parameterValue = BannerSqlConstants.UNSPECIFIED_NUMBER
                            }
                            sqlQuery.setBigDecimal(parameterName, parameterValue)

                        } else if (jsonPropertyType in ["D","T"]) {
                            if (extractedExtensionProperty.valueWasMissing) {
                                parameterValue = BannerSqlConstants.UNSPECIFIED_DATE
                            }
                            sqlQuery.setDate(parameterName, parameterValue)
                        } else {
                            //Default to a string
                            if (extractedExtensionProperty.valueWasMissing) {
                                parameterValue = BannerSqlConstants.UNSPECIFIED_STRING
                            }
                            sqlQuery.setCharacter(parameterName, parameterValue)
                        }
                    }
                }
            }
        }

        sqlQuery.executeUpdate()

        if (log.isTraceEnabled()) log.trace "extension.writeSuccessful"
    }
}
