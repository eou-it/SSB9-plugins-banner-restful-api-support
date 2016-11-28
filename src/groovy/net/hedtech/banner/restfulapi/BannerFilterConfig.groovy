/* ****************************************************************************
Copyright 2016 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.banner.restfulapi

import groovy.sql.Sql

import org.springframework.security.core.context.SecurityContextHolder

/**
 * Filter configuration for use with the 'restful-api' plugin.
 **/
abstract
class BannerFilterConfig {

    // must inject sessionFactory into this bean
    def sessionFactory

    final static GORDMSK_SQL =
        """SELECT field_pattern,
                  methods_not_allowed,
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
     * Retrieve filter configuration from the database.
     **/
    public Map retrieveFilterConfig(String resourceName) {
        assert sessionFactory != null
        def startTime = new Date()
        log.debug("ResourceName=$resourceName")
        def userId = SecurityContextHolder?.context?.authentication?.principal?.getOracleUserName()?.toUpperCase()
        log.debug("UserId=$userId")
        def filterConfig = [:]
        def sql = new Sql(sessionFactory.currentSession.connection())
        sql.eachRow(GORDMSK_SQL, [resourceName, userId, userId]) { row ->
            if (filterConfig.get(row.field_pattern) == null) {
                filterConfig.put(row.field_pattern,
                    [configEnabled: (row.display_ind == "N" ? true : false),
                     methodsNotAllowed: row.methods_not_allowed])
            }
        }
        log.debug("Elapsed retrieveFilterConfig time in ms: ${new Date().time - startTime.time}")
        return filterConfig
    }
}
