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
        guidResults.each { row ->
            buildingGuidList.add(row)
        }

        def querySQL = "select * from gorguid where gorguid_guid in (:GUID_LIST)"
        def resultList = readExecutionService.execute(querySQL,buildingGuidList)

        assertNotNull resultList
        assertTrue resultList.size == 5
    }
}
