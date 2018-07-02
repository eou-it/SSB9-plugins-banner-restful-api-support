/* ****************************************************************************
Copyright 2013-2018 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.integration.exception

/**
 * Holds a threadLocal Exception collector and manages its lifecycle.
 */
class ExceptionCollectorHolder {
    private static ThreadLocal<ExceptionCollector> threadLocal = new ThreadLocal();

    /**
     * Associates request params with a thread.
     * @params request parameters
     **/
    public static void init( ) {
        threadLocal.set( new ExceptionCollector());
    }


    /**
     * Returns the request parameters associated with the current thread.
     */
    public static ExceptionCollector get() {
        if (threadLocal.get() == null) {
            init()
        }

        return threadLocal.get()
    }


    /**
     * Clears any Locale associated with the current thread.  This MUST be
     * called prior to returning the thread to the pool.
     */
    public static void clear() {
        threadLocal.set( null );
    }

}
