/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.resource

/**
 * Representations supported by a resource.
 **/
public class SupportedRepresentation implements Serializable {

    String mediaType
    List<String> methods = []

}
