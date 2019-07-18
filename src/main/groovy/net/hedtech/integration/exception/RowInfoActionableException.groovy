/* ****************************************************************************
Copyright 2013-2018 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.integration.exception
/**
 * Exception raised when info on rows that failed can be provided back to the user for further follow up.
 */
class RowInfoActionableException  {

    /**
     * Original exception that was thrown for the record
     */
    public Throwable wrappedException
    /**
     * Actionable id. The user should be bale to take it and look up a record in the Authorative system with it.
     * E.g. The actual surrogate id for an address.
     */
    public String actionableId
    /**
     * Related GUID
     */
    public String guid

    /**
     * Entity/resource name
     */
    public String resourceName
}
