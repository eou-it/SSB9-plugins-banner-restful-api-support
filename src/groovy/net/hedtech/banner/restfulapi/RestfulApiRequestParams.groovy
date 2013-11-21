/* ****************************************************************************
Copyright 2013 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.banner.restfulapi

class RestfulApiRequestParams {
    private static ThreadLocal threadLocal = new ThreadLocal();


    /**
     * Associates request params with a thread.
     * @params request parameters
     **/
    public static void set( Map params ) {
        threadLocal.set( params);
    }


    /**
     * Returns the request parameters associated with the current thread.
     */
    public static Map get() {
        if (threadLocal.get() != null) {
            return threadLocal.get();
        } else {
            [:]
        }
    }


    /**
     * Clears any Locale associated with the current thread.  This MUST be
     * called prior to returning the thread to the pool.
     */
    public static void clear() {
        threadLocal.set( null );
    }


    /**
     * Returns a string representation of the params information
     */
    public static String getString() {
        StringBuffer sb = new StringBuffer();
        sb.append( "RestfulApiRequestParams " + RestfulApiRequestParams.get()  );
        return sb.toString();
    }
}