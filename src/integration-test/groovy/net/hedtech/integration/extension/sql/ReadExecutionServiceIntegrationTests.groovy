/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension.sql

import groovy.sql.Sql
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.text.SimpleDateFormat

/**
 * Created by sdorfmei on 5/15/17.
 */
class ReadExecutionServiceIntegrationTests  extends BaseIntegrationTestCase {

    def readExecutionService

    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    public void tearDown() {
        super.tearDown()
    }

    @Test
    void whenValidExpectResults() {
        //Get a GUID by looking at GORGUID and grabbing one (support for every developers GUIDs)
        def buildingGuidList = []
        def guidQuery = "SELECT * FROM (select gorguid_guid from gorguid where gorguid_ldm_name = 'buildings') gorguid WHERE rownum <= 5 ORDER BY rownum"
        def sqlQuery = sessionFactory.currentSession.createSQLQuery(guidQuery)
        def guidResults = sqlQuery.list()
        assertEquals 5, guidResults.size()
        guidResults.each { row ->
            buildingGuidList.add(row)
        }

        def querySQL = "select * from gorguid where gorguid_guid in (:GUID_LIST)"
        def resultList = readExecutionService.execute(querySQL,buildingGuidList)

        assertNotNull resultList
        assertTrue resultList.size == 5
    }

    @Test
    void when1000Guids() {
        //Get 1000 GUIDs to test limitations of SQL statement
        def largeGuidList = []
        def guidQuery = "SELECT * FROM (select gorguid_guid from gorguid where gorguid_ldm_name = 'persons') gorguid WHERE rownum <= 1000 ORDER BY rownum"
        def sqlQuery = sessionFactory.currentSession.createSQLQuery(guidQuery)
        def guidResults = sqlQuery.list()
        assertEquals 1000, guidResults.size()
        guidResults.each { row ->
            largeGuidList.add(row)
        }
        assertEquals 1000, largeGuidList.size()

        def querySQL = "select * from gorguid where gorguid_guid in (:GUID_LIST)"
        def resultList = readExecutionService.execute(querySQL,largeGuidList)

        assertNotNull resultList
        assertEquals largeGuidList.size(), resultList.size
        assertEquals largeGuidList.sort(), resultList.GORGUID_GUID.sort()
    }

    @Test
    void whenDifferentDataTypesFromSql() {
        // pre-set the expected string, number, date values
        def updateQuery = "update stvmrtl set stvmrtl_desc = 'updated desc', stvmrtl_version = 999999, stvmrtl_activity_date = to_date('2017-06-25','YYYY-MM-DD') where stvmrtl_code = 'S'"
        def sqlQuery = sessionFactory.currentSession.createSQLQuery(updateQuery)
        int updateCount = sqlQuery.executeUpdate()
        assertEquals 1, updateCount

        //Get a GUID by looking at GORGUID and grabbing one (support for every developers GUIDs)
        def maritalStatusGuidList = []
        def guidQuery = "select gorguid_guid from gorguid where gorguid_ldm_name = 'marital-status' and gorguid_domain_key = 'S'"
        sqlQuery = sessionFactory.currentSession.createSQLQuery(guidQuery)
        def guidResults = sqlQuery.list()
        assertEquals 1, guidResults.size()
        guidResults.each { row ->
            maritalStatusGuidList.add(row)
        }

        def querySql = '''select gorguid_guid as guid,
                                 stvmrtl_desc as my_string,
                                 stvmrtl_version as my_number,
                                 stvmrtl_activity_date as my_date
                            from gorguid, stvmrtl
                           where gorguid_ldm_name = 'marital-status'
                             and gorguid_domain_surrogate_id = stvmrtl_surrogate_id
                             and gorguid_guid in (:GUID_LIST)'''
        def resultList = readExecutionService.execute(querySql,maritalStatusGuidList)
        assertNotNull resultList
        assertEquals 1, resultList.size()
        resultList.each { row ->
            assertEquals 'updated desc', row.MY_STRING
            assertEquals 999999, row.MY_NUMBER.toInteger()
            assertEquals "2017-06-25", new SimpleDateFormat("yyyy-MM-dd").format(row.MY_DATE)
        }
    }

    @Test
    void whenDifferentDataTypesFromSde() {
        //Get a GUID by looking at GORGUID and grabbing one (support for every developers GUIDs)
        def buildingGuidList = []
        def guidQuery = "select gorguid_guid from gorguid where gorguid_ldm_name = 'buildings' and gorguid_domain_key = 'BIOL'"
        def sqlQuery = sessionFactory.currentSession.createSQLQuery(guidQuery)
        def guidResults = sqlQuery.list()
        assertEquals 1, guidResults.size()
        guidResults.each { row ->
            buildingGuidList.add(row)
        }

        // pre-set the expected string, number, date values
        def updateQuery = '''
                declare
                  CURSOR rowid_c (p_guid VARCHAR2) IS
                    SELECT s.rowid
                      FROM gorguid g, slbbldg s
                     WHERE g.gorguid_ldm_name = 'buildings'
                       AND g.gorguid_domain_surrogate_id = s.slbbldg_surrogate_id
                       AND g.gorguid_guid = p_guid;
                  lv_rowid   VARCHAR2(18);
                  lv_result  VARCHAR2(500);
                  lv_pk      gorsdav.gorsdav_pk_parenttab%TYPE;
                  lv_anydata SYS.ANYDATA;
                begin
                  OPEN rowid_c(:GUID);
                  FETCH rowid_c INTO lv_rowid;
                  CLOSE rowid_c;
                  select gp_goksdif.f_get_pk('SLBBLDG', lv_rowid) into lv_pk from dual;
                  lv_anydata := SYS.ANYDATA.convertVarchar2(:HEDM_BLDG_LANDMARK);
                  lv_result := gokhedm.f_save_sde_data(p_table   => 'SLBBLDG',
                                                       p_column  => 'HEDM_BLDG_LANDMARK',
                                                       p_rowid   => lv_rowid,
                                                       p_pk      => lv_pk,
                                                       p_value   => lv_anydata);
                  lv_anydata := SYS.ANYDATA.convertNumber(:HEDM_BLDG_ROOM_COUNT);
                  lv_result := gokhedm.f_save_sde_data(p_table   => 'SLBBLDG',
                                                       p_column  => 'HEDM_BLDG_ROOM_COUNT',
                                                       p_rowid   => lv_rowid,
                                                       p_pk      => lv_pk,
                                                       p_value   => lv_anydata);
                  lv_anydata := SYS.ANYDATA.convertDate(:HEDM_BLDG_CONSTR_DATE);
                  lv_result := gokhedm.f_save_sde_data(p_table   => 'SLBBLDG',
                                                       p_column  => 'HEDM_BLDG_CONSTR_DATE',
                                                       p_rowid   => lv_rowid,
                                                       p_pk      => lv_pk,
                                                       p_value   => lv_anydata);
                end;'''
        //updateQuery = updateQuery.replaceAll(":=", "\\\\:=")    // must escape the ':=' with '\:=' to allow for hibernate binding
        sqlQuery = sessionFactory.currentSession.createSQLQuery(updateQuery)
        sqlQuery.setString('GUID', buildingGuidList[0])
        sqlQuery.setString('HEDM_BLDG_LANDMARK', "Burger King")
        sqlQuery.setBigDecimal('HEDM_BLDG_ROOM_COUNT', 76)
        sqlQuery.setDate('HEDM_BLDG_CONSTR_DATE', new SimpleDateFormat("yyyy-MM-dd").parse("1982-01-05"))
        sqlQuery.executeUpdate()
        sessionFactory.currentSession.clear()   // must flush the current session

        def querySql = '''
                select gorguid_guid as guid,
                       sys.anydata.accessVarchar2(g1.gorsdav_value) as HEDM_BLDG_LANDMARK,
                       sys.anydata.accessNumber(g2.gorsdav_value) as HEDM_BLDG_ROOM_COUNT,
                       sys.anydata.accessDate(g3.gorsdav_value) as HEDM_BLDG_CONSTR_DATE
                  from gorguid g, slbbldg s, gorsdav g1, gorsdav g2, gorsdav g3
                 where gorguid_ldm_name = 'buildings'
                   and g.gorguid_domain_surrogate_id = s.slbbldg_surrogate_id
                   and g1.gorsdav_table_name(+) = 'SLBBLDG'
                   and g2.gorsdav_table_name(+) = 'SLBBLDG'
                   and g3.gorsdav_table_name(+) = 'SLBBLDG'
                   and g1.gorsdav_attr_name(+) = 'HEDM_BLDG_LANDMARK'
                   and g2.gorsdav_attr_name(+) = 'HEDM_BLDG_ROOM_COUNT'
                   and g3.gorsdav_attr_name(+) = 'HEDM_BLDG_CONSTR_DATE'
                   and g1.gorsdav_pk_parenttab(+) = s.slbbldg_bldg_code
                   and g2.gorsdav_pk_parenttab(+) = s.slbbldg_bldg_code
                   and g3.gorsdav_pk_parenttab(+) = s.slbbldg_bldg_code
                   and gorguid_guid in (:GUID_LIST)'''
        def resultList = readExecutionService.execute(querySql,buildingGuidList)
        assertNotNull resultList
        assertEquals 1, resultList.size()
        resultList.each { row ->
            assertEquals 'Burger King', row.HEDM_BLDG_LANDMARK
            assertEquals 76, row.HEDM_BLDG_ROOM_COUNT.toInteger()
            assertEquals "1982-01-05", new SimpleDateFormat("yyyy-MM-dd").format(row.HEDM_BLDG_CONSTR_DATE)
        }
    }
}
