/* ****************************************************************************
Copyright 2013 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.banner.restfulapi

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.service.ServiceBase
import net.hedtech.restfulapi.RestfulServiceAdapter

/**
 * An service adapter implementation for use with the 'restful-api' plugin.
 * The RESTful API Grails Plugin delegates to transactional services,
 * however uses a slightly different contract with services than that
 * used within Banner XE (and specifically exposed by ServiceBase).
 * If this adapter is registered in Spring IoC (e.g., in resources.groovy)
 * it will be used by the RestfulApiController when delegating to services.
 **/
class RestfulApiServiceBaseAdapter implements RestfulServiceAdapter {


    /**
     * Returns a list of domain object instances satisfying the params.
     * It is particularly important to include a 'max' params property
     * for paging, as this will result in a PagedResultList being returned.
     * The PagedResultList will include a totalCount, which will be used
     * by the RestfulApiController versus invoking the 'count' method below.
     **/
    def list(def service, Map params) {
        def startDate = new Date()
        def resultSize = null
        try {
            RestfulApiRequestParams.set(params)
            def results = service.list(params)
            if (results instanceof List) resultSize = results.size()
            return results
        } catch (ApplicationException ae) {
            throw ae // we'll let this pass through
        } catch (e) {
            def nfe = ServiceBase.extractNestedNotFoundException(e)
            if (nfe) throw new SimpleApplicationException( nfe )
            else     throw e
        } finally {
            RestfulApiRequestParams.clear()
            RestfulApiServiceMetrics.logMetrics(service, "list", startDate, new Date(), resultSize)
        }
    }

    /**
     * Returns a count of the domain class.
     * Note that, depending on your transaction demarcation, this may
     * execute within a separate transaction from the 'list' invocation.
     * This method is exposed to handle cases where the 'list' results
     * do not provide the total count. Note the some implementations
     * of 'list' (e.g., using Criteria) will return a PagedResultList.
     * When a PagedResultList is returned, the RestfulApiController will
     * use that (which contains the total count) versus calling this method.
     **/
    def count(def service, Map params) {
        def startDate = new Date()
        try {
            RestfulApiRequestParams.set(params)
            if (service.metaClass.respondsTo(service, "count", Map)) {
                service.count(params)
            } else {
                service.count()
            }
        } catch (ApplicationException ae) {
            throw ae // we'll let this pass through
        } catch (e) {
            def nfe = ServiceBase.extractNestedNotFoundException(e)
            if (nfe) throw new SimpleApplicationException( nfe )
            else     throw e
        } finally {
            RestfulApiRequestParams.clear()
            RestfulApiServiceMetrics.logMetrics(service, "count", startDate, new Date())
        }
    }

    /**
     * Returns the domain object instance identified by 'params.id'.
     * Note: This 'show(params)' method will delegate to the service's
     * 'get(id) method.
     **/
    def show(def service, Map params) {
        def startDate = new Date()
        try {
            RestfulApiRequestParams.set(params)
            service.get(params.id)
        } catch (ApplicationException ae) {
            throw ae // we'll let this pass through
        } catch (e) {
            def nfe = ServiceBase.extractNestedNotFoundException(e)
            if (nfe) throw new SimpleApplicationException( nfe )
            else     throw e
        } finally {
            RestfulApiRequestParams.clear()
            RestfulApiServiceMetrics.logMetrics(service, "show", startDate, new Date())
        }
    }

    /**
     * Creates a new instance of the domain object.
     **/
    def create(def service, Map content, Map params) {
        def startDate = new Date()
        try {
            RestfulApiRequestParams.set(params)
            service.create(content)
        } catch (ApplicationException ae) {
            throw ae // we'll let this pass through
        } catch (e) {
            def nfe = ServiceBase.extractNestedNotFoundException(e)
            if (nfe) throw new SimpleApplicationException( nfe )
            else     throw e
        } finally {
            RestfulApiRequestParams.clear()
            RestfulApiServiceMetrics.logMetrics(service, "create", startDate, new Date())
        }
    }

    /**
     * Updates an existing domain object instance.
     * Note: When this method delegates to ServiceBase's 'update' method,
     * it only passes the 'content' map.
     **/
    def update(def service, def id, Map content, Map params) {
        def startDate = new Date()
        try {
            RestfulApiRequestParams.set(params)
            if (!content.id) content.id = id
            service.update(content)
        } catch (ApplicationException ae) {
            throw ae // we'll let this pass through
        } catch (e) {
            def nfe = ServiceBase.extractNestedNotFoundException(e)
            if (nfe) throw new SimpleApplicationException( nfe )
            else     throw e
        } finally {
            RestfulApiRequestParams.clear()
            RestfulApiServiceMetrics.logMetrics(service, "update", startDate, new Date())
        }
    }

    /**
     * Deletes an existing domain object instance.
     * Note: When this method delegates to ServiceBase's 'delete' method,
     * it only passes the 'content' map.
     **/
    void delete(def service, def id, Map content, Map params) {
        def startDate = new Date()
        try {
            RestfulApiRequestParams.set(params)
            if (!content.id) content.id = id
            service.delete(content)
        } catch (ApplicationException ae) {
            throw ae // we'll let this pass through
        } catch (e) {
            def nfe = ServiceBase.extractNestedNotFoundException(e)
            if (nfe) throw new SimpleApplicationException( nfe )
            else     throw e
        } finally {
            RestfulApiRequestParams.clear()
            RestfulApiServiceMetrics.logMetrics(service, "delete", startDate, new Date())
        }
    }
}


// Exceptions are generally expected to be wrapped within an ApplicationException. However, there are
// some exceptions that may occur which will not be wrapped as such, and must be handled here.
// One such exception is MepCodeNotFoundException, which we'll handle here to ensure it is reported
// as desired (i.e., as a '404 Not Found'.
//
class SimpleApplicationException extends RuntimeException {

    private httpStatusCode = 500
    private e // the wrapped exception

    public def returnMap = { localize ->
              [ message: e?.message, // we won't localized unknown exceptions...
                errors: null
              ]
    }

    public SimpleApplicationException(Throwable e) {
        this.e = e
        if (e?.class?.name =~ "MepCodeNotFound") {
            this.httpStatusCode = 404
            returnMap = { localize ->
                          [ message: localize( code: "default.mepcode.not.found.message",
                                               args: [e?.hasProperty('mepCode') ? e?.mepCode : null ] ),
                            errors: null
                          ]
                        }
        }
    }

    public int getHttpStatusCode() {
        httpStatusCode
    }

    public String getMessage() {
        e ? e.message : "SimpleApplicationException"
    }

    public String toString() {
        "SimpleApplicationException[e=$e] "
    }

}
