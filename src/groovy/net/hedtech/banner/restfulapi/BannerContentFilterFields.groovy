/* ****************************************************************************
Copyright 2016 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.ContentFilterFields

import groovy.sql.Sql

import org.springframework.security.core.context.SecurityContextHolder

/**
 * A content filter fields implementation for use with the 'restful-api' plugin.
 **/
class BannerContentFilterFields implements ContentFilterFields {

    // must inject sessionFactory into this bean
    def sessionFactory

    final static GORDMSK_SQL =
        """SELECT field_pattern,
                  fgac_user_id,
                  display_ind,
                  group_fgac_user_id,
                  all_user_ind
             FROM gvq_rest_content_filter
            WHERE resource_name = ?
              AND (fgac_user_id = ? OR group_fgac_user_id = ? OR all_user_ind = 'Y')"""


    /**
     * Retrieve list of field patterns to be filtered from content.
     **/
    public List retrieveFieldPatterns(String resourceName) {
        def startTime = new Date()
        log.debug("ResourceName=$resourceName")
        def userId = SecurityContextHolder?.context?.authentication?.principal?.getOracleUserName()?.toUpperCase()
        log.debug("UserId=$userId")
        def fieldPatternsByUser = [:]
        def fieldPatternsByGroupUser = [:]
        def fieldPatternsByAllUsers = [:]
        def sql = new Sql(sessionFactory.currentSession.connection())
        sql.eachRow(GORDMSK_SQL, [resourceName, userId, userId]) { row ->
            if (row.fgac_user_id != null) {
                fieldPatternsByUser.put(row.field_pattern, row.display_ind)
            } else if (row.group_fgac_user_id != null) {
                fieldPatternsByGroupUser.put(row.field_pattern, row.display_ind)
            } else if (row.all_user_ind == "Y") {
                fieldPatternsByAllUsers.put(row.field_pattern, row.display_ind)
            }
        }
        def prioritizedFieldPatterns = [:]
        prioritizedFieldPatterns.putAll(fieldPatternsByAllUsers)
        prioritizedFieldPatterns.putAll(fieldPatternsByGroupUser)
        prioritizedFieldPatterns.putAll(fieldPatternsByUser)
        def fieldPatterns = []
        prioritizedFieldPatterns.each {
            if (it.value == "N") {
                fieldPatterns.add(it.key)
            }
        }
        fieldPatterns = fieldPatterns.unique().sort()
        log.debug("Field patterns=$fieldPatterns")
        log.debug("Elapsed retrieveFieldPatterns time in ms: ${new Date().time - startTime.time}")
        return fieldPatterns
    }
}
