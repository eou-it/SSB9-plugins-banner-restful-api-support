/* ****************************************************************************
Copyright 2016-2020 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.banner.restfulapi

import groovy.sql.Sql

import org.springframework.security.core.context.SecurityContextHolder

/**
 * Filter configuration for use with the 'restful-api' plugin.
 **/
import groovy.util.logging.Slf4j
@ Slf4j
abstract
class BannerFilterConfig {

    // must inject sessionFactory into this bean
    def sessionFactory

    final static GORDMSK_SQL =
        """SELECT field_pattern,
                  status_ind,
                  fgac_user_id,
                  group_fgac_code,
                  group_fgac_user_id,
                  all_user_ind
             FROM gvq_rest_filter_config
            WHERE resource_name = ?
              AND (fgac_user_id = ? OR group_fgac_user_id = ? OR all_user_ind = 'Y')
         ORDER BY field_pattern, fgac_user_id, group_fgac_code, all_user_ind"""

    final static GORDMSK_FOR_EMS_API_USERS_SQL =
        """SELECT field_pattern
             FROM gvq_rest_filter_config
            WHERE resource_name = ?
              AND status_ind = ?"""

    final static GORICCR_SQL =
            """SELECT goriccr_translation_value
             FROM gv_goriccr
            WHERE goriccr_sqpr_code = ?
              AND goriccr_icsn_code = ?"""


    /**
     * Retrieve filter configuration from the database.
     **/
    public Map retrieveFilterConfig(String resourceName) {
        assert sessionFactory != null
        def startTime = new Date()
        log.debug("ResourceName=$resourceName")
        def userId = SecurityContextHolder?.context?.authentication?.principal?.getOracleUserName()?.toUpperCase()
        log.debug("UserId=$userId")
        def emsApiUsernames = retrieveEmsApiUsernames()
        def filterConfig = [:]
        def sql = new Sql(sessionFactory.currentSession.connection())
        if (emsApiUsernames.contains(userId)) {
            sql.eachRow(GORDMSK_FOR_EMS_API_USERS_SQL, [resourceName, "A"]) { row ->
                if (filterConfig.get(row.field_pattern) == null) {
                    filterConfig.put(row.field_pattern, [configActive: true])
                }
            }
        } else {
            sql.eachRow(GORDMSK_SQL, [resourceName, userId, userId]) { row ->
                if (filterConfig.get(row.field_pattern) == null) {
                    filterConfig.put(row.field_pattern, [configActive: (row.status_ind == "A")])
                }
            }
        }
        log.debug("Elapsed retrieveFilterConfig time in ms: ${new Date().time - startTime.time}")
        return filterConfig
    }


    /**
     * Retrieve list of usernames for the Ethos Integration Messaging Service.
     **/
    public List retrieveEmsApiUsernames() {
        List emsApiUsernames = []
        def sql = new Sql(sessionFactory.currentSession.connection())
        sql.eachRow(GORICCR_SQL, ["EMS-ETHOS-INTEGRATION", "EMS.API.USERNAME"]) { row ->
            emsApiUsernames.add(row.goriccr_translation_value?.toUpperCase())
        }
        log.debug "EMS API usernames $emsApiUsernames configured in GORICCR"
        return emsApiUsernames
    }
}
