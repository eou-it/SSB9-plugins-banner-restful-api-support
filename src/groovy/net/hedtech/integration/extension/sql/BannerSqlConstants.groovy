/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.integration.extension.sql

import java.text.SimpleDateFormat

/**
 * Banner Sql Constants
 */
class BannerSqlConstants {

    // Banner Sql constants taken from DML_COMMON package
    static final UNSPECIFIED_STRING = 1 as char
    static final UNSPECIFIED_NUMBER = 1E-35
    static final UNSPECIFIED_DATE = new SimpleDateFormat("yyyy-MM-dd").parse("1000-01-01")

}
