/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension.sql

import net.hedtech.banner.testing.BaseIntegrationTestCase
import net.hedtech.integration.extension.ExtensionDefinition
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
/*
    @Test
    void testCall() {
/*
        String sql =
                 "select gorguid_guid as guid, slbbldg_maximum_capacity from gorguid,slbbldg where gorguid_ldm_name = 'buildings' and gorguid_domain_surrogate_id = slbbldg_surrogate_id and gorguid_guid in (:GUID_LIST)"

        def guidList = ['24c47f0a-0eb7-48a3-85a6-2c585691c6ce','26a2673f-9bc6-4649-a3e8-213d0ff4afbd','acc93569-9275-47d4-986a-313f52ff8044']
        def results = readExecutionService.execute(sql,guidList)

        results.each { row ->
            println("GUID=" + row.GUID + "; " + "SLBBLDG_MAXIMUM_CAPACITY=" + row["SLBBLDG_MAXIMUM_CAPACITY"])
        }


    }*/
}
