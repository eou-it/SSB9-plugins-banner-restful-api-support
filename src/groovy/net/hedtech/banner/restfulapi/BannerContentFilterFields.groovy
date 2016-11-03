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

    final static GORDMSK_SQL = """SELECT gordmsk_block_comp_name
  FROM gordmsk
 WHERE ROWID IN
       (SELECT substr(c.new_order,6)
          FROM (SELECT DISTINCT b.gordmsk_block_comp_name
                               ,MIN(b.my_order) "NEW_ORDER"
                  FROM (SELECT a.*,to_char(rownum,'009') || '-' || a.ROWID "MY_ORDER"
                          FROM (SELECT gordmsk_block_comp_name
                                      ,gordmsk_column_comp_name
                                      ,gordmsk_all_user_ind
                                      ,gordmsk_fbpr_code
                                      ,gordmsk_fgac_user_id
                                  FROM gordmsk
                                 WHERE gordmsk_objs_code  = ?
                                   AND gordmsk_block_name = 'API'
                                   AND (gordmsk_fgac_user_id IS NULL OR
                                       gordmsk_fgac_user_id = ?)
                                   AND (gordmsk_fbpr_code IS NULL OR gordmsk_fbpr_code IN
                                                       (SELECT gorfbpr_fbpr_code
                                                          FROM gorfbpr
                                                         WHERE gorfbpr_fgac_user_id = ?))
                                   AND gordmsk_display_ind = 'N'
                                 ORDER BY gordmsk_block_comp_name
                                         ,gordmsk_fgac_user_id
                                         ,gordmsk_fbpr_code
                                         ,gordmsk_all_user_ind DESC
                               ) a
                       ) b
                 GROUP BY gordmsk_block_comp_name
               ) c
       )
 ORDER BY gordmsk_block_comp_name"""


    /**
     * Retrieve list of fields or field patterns to be filtered from content.
     **/
    public List retrieveFields(String resourceName) {
        def startTime = new Date()
        def userId = SecurityContextHolder?.context?.authentication?.principal?.getOracleUserName()?.toUpperCase()
        def objectCode = '**API_' + resourceName.toUpperCase()
        log.debug("UserId=$userId")
        log.debug("ObjectCode=$objectCode")
        def fields = []
        def sql = new Sql(sessionFactory.currentSession.connection())
        sql.eachRow(GORDMSK_SQL, [objectCode, userId, userId]) { row ->
            fields.add(row.gordmsk_block_comp_name)
        }
        log.debug("Fields=$fields")
        log.debug("Elapsed retrieveFields time in ms: ${new Date().time - startTime.time}")
        return fields
    }
}
