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
                  display_ind,
                  fgac_user_id,
                  group_fgac_code,
                  group_fgac_user_id,
                  all_user_ind
             FROM gvq_rest_content_filter
            WHERE resource_name = ?
              AND (fgac_user_id = ? OR group_fgac_user_id = ? OR all_user_ind = 'Y')
         ORDER BY field_pattern, fgac_user_id, group_fgac_code, all_user_ind"""


    /**
     * Retrieve list of field patterns to be filtered from content.
     **/
    public List retrieveFieldPatterns(String resourceName) {
        assert sessionFactory != null
        def startTime = new Date()
        log.debug("ResourceName=$resourceName")
        def userId = SecurityContextHolder?.context?.authentication?.principal?.getOracleUserName()?.toUpperCase()
        log.debug("UserId=$userId")
        def prioritizedFieldPatterns = [:]
        def sql = new Sql(sessionFactory.currentSession.connection())
        sql.eachRow(GORDMSK_SQL, [resourceName, userId, userId]) { row ->
            if (prioritizedFieldPatterns.get(row.field_pattern) == null &&
                    (row.fgac_user_id != null ||
                     row.group_fgac_user_id != null ||
                     row.all_user_ind == "Y")) {
                prioritizedFieldPatterns.put(row.field_pattern, row.display_ind)
            }
        }
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
