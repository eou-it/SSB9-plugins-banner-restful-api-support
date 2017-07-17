/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension.sql

import groovy.sql.Sql
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
                 update stvmrtl
                    set stvmrtl_fa_conv_code = :STVMRTL_FA_CONV_CODE,
                        stvmrtl_edi_equiv = :STVMRTL_EDI_EQUIV,
                        stvmrtl_version = stvmrtl_version + 1,
                        stvmrtl_activity_date = SYSDATE
                  where stvmrtl_surrogate_id = (select gorguid_domain_surrogate_id
                                                  from gorguid
                                                 where gorguid_ldm_name = 'marital-status'
                                                   and gorguid_guid = :GUID);
               end;"""
        writeExecutionService.execute(writeSql, maritalStatusGuidList[0], [STVMRTL_FA_CONV_CODE:'A', STVMRTL_EDI_EQUIV:'B'])

        def verifyQuery = "select stvmrtl_fa_conv_code, stvmrtl_edi_equiv from stvmrtl where stvmrtl_code = 'S'"
        sqlQuery = sessionFactory.currentSession.createSQLQuery(verifyQuery)
        def verifyResults = sqlQuery.list()
        assertEquals 1, verifyResults.size()
        verifyResults.each { row ->
            assertEquals 'A', row[0]
            assertEquals 'B', row[1]
        }
    }
}
