/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.extension

/**
 * Class used to group Extension Definitions by a group so that we only should
 * call the database once per group vs for every extension definition
 */
class ExtensionDefinitionGroup {

    String sqlProcesCode
    String sqlRuleCode

    List<ExtensionDefinition> extensionDefinitionList
}
