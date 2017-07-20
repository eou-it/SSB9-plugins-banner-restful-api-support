/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension.sql

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * WriteExecutionService tests.
 */
class WriteExecutionServiceIntegrationTests  extends BaseIntegrationTestCase {

    def writeExecutionService

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
        def maritalStatusGuidList = []
        def guidQuery = "select gorguid_guid from gorguid where gorguid_ldm_name = 'marital-status' and gorguid_domain_key = 'S'"
        def sqlQuery = sessionFactory.currentSession.createSQLQuery(guidQuery)
        def guidResults = sqlQuery.list()
        assertEquals 1, guidResults.size()
        guidResults.each { row ->
            maritalStatusGuidList.add(row)
        }

        def writeSql =
            """begin
                 if (:HTTP_METHOD = 'PUT') then
                     update stvmrtl
                        set stvmrtl_fa_conv_code = :STVMRTL_FA_CONV_CODE,
                            stvmrtl_edi_equiv = :STVMRTL_EDI_EQUIV,
                            stvmrtl_version = stvmrtl_version + 1,
                            stvmrtl_activity_date = SYSDATE
                      where stvmrtl_surrogate_id = (select gorguid_domain_surrogate_id
                                                      from gorguid
                                                     where gorguid_ldm_name = 'marital-status'
                                                       and gorguid_guid = :GUID);
                  end if;                       
               end;"""
        writeExecutionService.execute(writeSql, maritalStatusGuidList[0], "PUT",[STVMRTL_FA_CONV_CODE:'A', STVMRTL_EDI_EQUIV:'B'])

        def verifyQuery = "select stvmrtl_fa_conv_code, stvmrtl_edi_equiv from stvmrtl where stvmrtl_code = 'S'"
        sqlQuery = sessionFactory.currentSession.createSQLQuery(verifyQuery)
        def verifyResults = sqlQuery.list()
        assertEquals 1, verifyResults.size()
        verifyResults.each { row ->
            assertEquals 'A', row[0]
            assertEquals 'B', row[1]
        }
    }

    @Test
    void whenUnspecifiedValues() {
        //Get a GUID by looking at GORGUID and grabbing one (support for every developers GUIDs)
        def maritalStatusGuidList = []
        def guidQuery = "select gorguid_guid from gorguid where gorguid_ldm_name = 'marital-status' and gorguid_domain_key = 'S'"
        def sqlQuery = sessionFactory.currentSession.createSQLQuery(guidQuery)
        def guidResults = sqlQuery.list()
        assertEquals 1, guidResults.size()
        guidResults.each { row ->
            maritalStatusGuidList.add(row)
        }

        def writeSql =
                """begin
                    if (:HTTP_METHOD = 'PUT') then
                         if :GUID != 'test' then
                           raise_application_error (-20001,'Missing GUID parameter');
                         end if;
                         if :UNSPECIFIED_STRING != dml_common.unspecified_string then
                           raise_application_error (-20001,'Expected unspecified string '||dml_common.unspecified_string||', but was '||:UNSPECIFIED_STRING);
                         end if;
                         if :UNSPECIFIED_NUMBER != dml_common.unspecified_number then
                           raise_application_error (-20001,'Expected unspecified number '||dml_common.unspecified_number||', but was '||:UNSPECIFIED_NUMBER);
                         end if;
                         if :UNSPECIFIED_DATE != dml_common.unspecified_date then
                           raise_application_error (-20001,'Expected unspecified date '||dml_common.unspecified_date||', but was '||:UNSPECIFIED_DATE);
                         end if;
                     end if;
               end;"""
        writeExecutionService.execute(writeSql, "test", "PUT",[
                UNSPECIFIED_STRING: BannerSqlConstants.UNSPECIFIED_STRING,
                UNSPECIFIED_NUMBER: BannerSqlConstants.UNSPECIFIED_NUMBER,
                UNSPECIFIED_DATE: BannerSqlConstants.UNSPECIFIED_DATE])
    }
}
