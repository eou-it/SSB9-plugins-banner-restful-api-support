/*******************************************************************************
 Copyright 2016-2020 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

package net.hedtech.banner.restfulapi

import net.hedtech.banner.testing.BaseIntegrationTestCase

import groovy.sql.Sql

/**
 * Test data for BannerFilterConfig
 */

 class BannerFilterConfigTestData extends BaseIntegrationTestCase {


    void teardownTestData() {
        def sql = new Sql(sessionFactory.currentSession.connection())
        sql.execute("delete from gordmsk where gordmsk_block_name = ?",['API'])
        updateIntegrationConfiguration("EMS-ETHOS-INTEGRATION", "EMS.API.USERNAME", "UPDATE ME")
    }


    int verifyCount() {
        def sql = new Sql(sessionFactory.currentSession.connection())
        return sql.firstRow("select count(*) as cnt from gordmsk where gordmsk_block_name = ?",['API']).cnt.intValue()
    }


    void createTestData(List testData) {
        StringBuffer cmd = new StringBuffer(BEGIN_SQL_TEMPLATE)
        testData.each {
            cmd.append('  create_grodmsk(\'')
            cmd.append(it.resourceName)
            cmd.append('\', \'')
            cmd.append(it.fieldPattern)
            cmd.append('\', \'')
            cmd.append(it.methodsNotAllowed)
            cmd.append('\', ')
            cmd.append(it.seqno)
            cmd.append(', \'')
            cmd.append(it.statusInd)
            cmd.append('\', \'')
            cmd.append(it.userPattern)
            cmd.append('\');\n')
        }
        cmd.append(END_SQL_TEMPLATE)
        def sql = new Sql(sessionFactory.currentSession.connection())
        sql.call(cmd.toString())
    }


    final static String BEGIN_SQL_TEMPLATE =
        """
DECLARE


  PROCEDURE create_gtvfbpr(p_code  gtvfbpr.gtvfbpr_code%TYPE,
                           p_desc  gtvfbpr.gtvfbpr_desc%TYPE) IS

  BEGIN

    INSERT into gtvfbpr
       (gtvfbpr_code,
        gtvfbpr_desc,
        gtvfbpr_activity_date,
        gtvfbpr_user_id)
      SELECT
        p_code,
        p_desc,
        SYSDATE,
        USER
       FROM dual
      WHERE NOT EXISTS
        (SELECT 1
           FROM gtvfbpr
          WHERE gtvfbpr_code = p_code);

  END;


  PROCEDURE create_gorfbpr(p_fgac_user_id  gorfbpr.gorfbpr_fgac_user_id%TYPE,
                           p_fbpr_code     gorfbpr.gorfbpr_fbpr_code%TYPE) IS

  BEGIN

    INSERT into gorfbpr
       (gorfbpr_fgac_user_id,
        gorfbpr_fbpr_code,
        gorfbpr_activity_date,
        gorfbpr_user_id)
      SELECT
        p_fgac_user_id,
        p_fbpr_code,
        SYSDATE,
        USER
       FROM dual
      WHERE NOT EXISTS
        (SELECT 1
           FROM gorfbpr
          WHERE gorfbpr_fgac_user_id = p_fgac_user_id
            AND gorfbpr_fbpr_code = p_fbpr_code);

  END;


  PROCEDURE create_grodmsk(p_resource_name  gordmsk.gordmsk_objs_comp_name%TYPE,
                           p_field_pattern  gordmsk.gordmsk_block_comp_name%TYPE,
                           p_methods_not_allowed  gordmsk.gordmsk_column_comp_name%TYPE,
                           p_sqno           gordmsk.gordmsk_seqno%TYPE,
                           p_status_ind     gordmsk.gordmsk_display_ind%TYPE,
                           p_user_pattern   VARCHAR2) IS

    lv_objs_code           gordmsk.gordmsk_objs_code%TYPE;
    lv_block_name          gordmsk.gordmsk_block_name%TYPE;
    lv_column_name         gordmsk.gordmsk_column_name%TYPE;
    lv_fgac_user_id        gordmsk.gordmsk_fgac_user_id%TYPE;
    lv_group_fgac_user_id  gordmsk.gordmsk_fgac_user_id%TYPE;
    lv_fbpr_code           gordmsk.gordmsk_fbpr_code%TYPE;
    lv_all_user_ind        gordmsk.gordmsk_all_user_ind%TYPE;
    lv_methods_not_allowed gordmsk.gordmsk_column_comp_name%TYPE;
    lv_display_ind         gordmsk.gordmsk_display_ind%TYPE;

  BEGIN
    lv_objs_code := UPPER(REPLACE(SUBSTR('**API_'||p_resource_name,1,30),'-','_'));
    lv_block_name := 'API';
    lv_column_name := UPPER(REPLACE(REPLACE(REPLACE(p_field_pattern,'.','_'),'==','_'),'@',''));
    lv_methods_not_allowed := p_methods_not_allowed;
    IF (p_field_pattern = '*') THEN
      lv_column_name := 'HTTP_METHOD_NOT_ALLOWED_CODES';
    ELSE
      lv_methods_not_allowed := '*';
    END IF;
    IF (p_status_ind = 'A') THEN
      lv_display_ind := 'N';
    ELSIF (p_status_ind = 'I') THEN
      lv_display_ind := 'Y';
    ELSE
      lv_display_ind := null;
    END IF;

    -- check if this field pattern applies to all users
    IF (UPPER(p_user_pattern) = '*') THEN
      lv_fgac_user_id := NULL;
      lv_group_fgac_user_id := NULL;
      lv_fbpr_code := NULL;
      lv_all_user_ind := 'Y';
    ELSE

      -- check if this field pattern applies to a user in a group (ex: user_id:group_id)
      IF (INSTR(p_user_pattern,':') > 0) THEN
        lv_fgac_user_id := NULL;
        lv_group_fgac_user_id := SUBSTR(p_user_pattern,1,INSTR(p_user_pattern,':',1,1)-1);
        lv_fbpr_code := SUBSTR(p_user_pattern,INSTR(p_user_pattern,':',-1,1)+1);
        lv_all_user_ind := 'N';
      ELSE

        -- this field pattern applies to an individual user
        lv_fgac_user_id := p_user_pattern;
        lv_group_fgac_user_id := NULL;
        lv_fbpr_code := NULL;
        lv_all_user_ind := 'N';

      END IF;

    END IF;

    IF (lv_fbpr_code IS NOT NULL) THEN
      create_gtvfbpr(lv_fbpr_code, lv_fbpr_code);
      create_gorfbpr(lv_group_fgac_user_id, lv_fbpr_code);
    END IF;

    INSERT into gordmsk
       (gordmsk_objs_code,
        gordmsk_block_name,
        gordmsk_column_name,
        gordmsk_seqno,
        gordmsk_display_ind,
        gordmsk_conceal_ind,
        gordmsk_objs_comp_name,
        gordmsk_block_comp_name,
        gordmsk_column_comp_name,
        gordmsk_fgac_user_id,
        gordmsk_fbpr_code,
        gordmsk_all_user_ind,
        gordmsk_activity_date,
        gordmsk_user_id)
      SELECT
        lv_objs_code,
        lv_block_name,
        lv_column_name,
        p_sqno,
        lv_display_ind,
        'N',
        p_resource_name,
        p_field_pattern,
        lv_methods_not_allowed,
        lv_fgac_user_id,
        lv_fbpr_code,
        lv_all_user_ind,
        SYSDATE,
        USER
       FROM dual
      WHERE NOT EXISTS
        (SELECT 1
           FROM gordmsk
          WHERE gordmsk_objs_code = lv_objs_code
            AND gordmsk_block_name = lv_block_name
            AND gordmsk_column_name = lv_column_name
            AND gordmsk_seqno = p_sqno);

  END;


BEGIN

  delete from gordmsk where gordmsk_block_name = 'API';
  delete from gorfbpr where gorfbpr_fbpr_code like 'API_TEST%_FPBR';
  delete from gtvfbpr where gtvfbpr_code like 'API_TEST%_FPBR';

"""


    final static String END_SQL_TEMPLATE =
            """
END;
"""


    def updateIntegrationConfiguration(String processCode, String settingName, String translationValue) {
        def sql = new Sql(sessionFactory.currentSession.connection())
        sql.execute("update goriccr set goriccr_translation_value = ? where goriccr_sqpr_code = ? and goriccr_icsn_code = ?",[translationValue, processCode, settingName])
    }

}
