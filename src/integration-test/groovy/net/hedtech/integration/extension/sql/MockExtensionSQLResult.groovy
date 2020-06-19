/*******************************************************************************
 Copyright 2017-2020 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.extension.sql

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration

/**
 * Created by sdorfmei on 5/26/17.
 */
@Rollback
@Integration
class MockExtensionSQLResult {
    String GUID
    def columnName
}
