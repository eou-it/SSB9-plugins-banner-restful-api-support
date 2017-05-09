/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.resource

/**
 * Resources supported by an application.
 **/
public class SupportedResource implements Serializable {

    String name
    List<SupportedRepresentation> representations = new ArrayList<>()

}
