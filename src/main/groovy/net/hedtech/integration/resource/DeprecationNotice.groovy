/******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.resource

/**
 * Deprecation notice for a resource representation.
 **/
public class DeprecationNotice implements Serializable {

    String deprecatedOn
    String sunsetOn
    String description

}
