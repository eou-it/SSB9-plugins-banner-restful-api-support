/* ****************************************************************************
Copyright 2013-2018 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.integration.exception

import net.hedtech.banner.restfulapi.RestfulApiRequestParams

/**
 * Holds a threadLocal Exception collector and manages its lifecycle.
 */
class ExceptionCollectorHolder {

    final static String COLLECTOR_NAME = "net.hedtech.integration.exception.ExceptionCollectorHolder"

    /**
     * Returns the request parameters associated with the current thread.
     */
    public static ExceptionCollector get() {

        if(RestfulApiRequestParams.get() == null)
        {
            throw new RuntimeException("Initialize RestfulApiRequestParams params")
        }

        if(RestfulApiRequestParams.get()?.get(COLLECTOR_NAME) == null)
        {
            RestfulApiRequestParams.get()?.put(COLLECTOR_NAME, new ExceptionCollector())
        }


        return RestfulApiRequestParams.get().get(COLLECTOR_NAME)
    }


}
