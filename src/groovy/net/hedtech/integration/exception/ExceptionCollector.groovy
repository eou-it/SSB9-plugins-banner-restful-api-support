/* ****************************************************************************
Copyright 2013-2017 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.integration.exception

import net.hedtech.restfulapi.Localizer

/**
 * Collects exception that were thrown during a loop
 */
class ExceptionCollector extends RuntimeException {
    private List<RowInfoActionableException> exceptionList = new ArrayList<>()

    def add(String resourceName, String guid, String actionableId, Exception exception) {
        RowInfoActionableException rowInfoEx = new RowInfoActionableException()
        rowInfoEx.wrappedException = exception
        rowInfoEx.actionableId = actionableId
        rowInfoEx.resourceName = resourceName
        rowInfoEx.guid=guid

        exceptionList.add(rowInfoEx)
    }

    /**
     * Will throw itself as an exception if there are errors
     */
    def check() {
        if (!exceptionList.isEmpty()) {
            throw this
        }
    }
}
