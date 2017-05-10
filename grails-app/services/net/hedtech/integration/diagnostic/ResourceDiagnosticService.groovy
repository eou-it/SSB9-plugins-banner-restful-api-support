/******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ******************************************************************************/
package net.hedtech.integration.diagnostic

import grails.transaction.Transactional
import net.hedtech.banner.query.DynamicFinder
import net.hedtech.banner.query.operators.Operators
import net.hedtech.banner.service.ServiceBase
import net.hedtech.integration.utility.RestfulApiValidationUtility
import org.springframework.transaction.annotation.Propagation

/**
 * A service to return resource diagnostic messages.
 **/
@Transactional
public class ResourceDiagnosticService extends ServiceBase {

    boolean transactional = true

    private static final int MAX_DEFAULT = RestfulApiValidationUtility.MAX_DEFAULT
    private static final int MAX_UPPER_LIMIT = RestfulApiValidationUtility.MAX_UPPER_LIMIT

    /**
     * GET /api/diagnostics
     *
     * @param params Request parameters
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    def list(Map params) {
        setPagingParams(params)
        List<ResourceDiagnosticMessage> dbResult = getDataFromDB(params)
        int totalRecordCount = getTotalCount(params)
        injectPropertyIntoParams(params, "totalCount", totalRecordCount)
        return dbResult
    }

    /**
     * GET /api/diagnostics
     *
     * The count method must return the total number of instances of the resource.
     * It is used in conjunction with the list method when returning a list of resources.
     * RestfulApiController will make call to "count" only if the "list" execution happens without any exception.
     *
     * @param params Request parameters
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    def count(Map params) {
        return getInjectedPropertyFromParams(params, "totalCount")
    }

    /**
     * POST /api/diagnostics
     *
     * @param content Request body
     */
    def create(Map content) {
        ResourceDiagnosticMessage.withSession { session ->
            session.getNamedQuery('ResourceDiagnosticMessage.submitDiagnosticsJob').executeUpdate()
        }
        return null
    }

    private void setPagingParams(Map params) {
        RestfulApiValidationUtility.correctMaxAndOffset(params, MAX_DEFAULT, MAX_UPPER_LIMIT)
        if (!params.containsKey("offset")) {
            params.put("offset", "0")
        }
    }

    private void injectPropertyIntoParams(Map params, String propName, def propVal) {
        def injectedProps = [:]
        if (params.containsKey("resource-diagnostics-injected") && params.get("resource-diagnostics-injected") instanceof Map) {
            injectedProps = params.get("resource-diagnostics-injected")
        } else {
            params.put("resource-diagnostics-injected", injectedProps)
        }
        injectedProps.putAt(propName, propVal)
    }

    private def getInjectedPropertyFromParams(Map params, String propName) {
        def propVal
        def injectedProps = params.get("resource-diagnostics-injected")
        if (injectedProps instanceof Map && injectedProps.containsKey(propName)) {
            propVal = injectedProps.get(propName)
        }
        return propVal
    }

    private List<ResourceDiagnosticMessage> getDataFromDB(Map params) {
        return fetchAllByCriteria(null, null, null, null, params.max.toInteger(), params.offset.toInteger())
    }

    private int getTotalCount(Map params) {
        return countByCriteria(null, null)
    }

    List<ResourceDiagnosticMessage> fetchAllByCriteria(Map content, List<String> filterPropertyNames, String sortField, String sortDirection = "asc", int max = 0, int offset = -1) {
        Map params = [:]
        List criteria = []
        Map pagingAndSortingParams = [:]

        buildCriteria(content, filterPropertyNames, params, criteria)

        if (max > 0) {
            pagingAndSortingParams.max = max
        }

        if (offset > -1) {
            pagingAndSortingParams.offset = offset
        }

        if (sortField) {
            pagingAndSortingParams.sortCriteria = [
                    ["sortColumn": sortField, "sortDirection": sortDirection],
                    ["sortColumn": 'id', "sortDirection": 'asc']
            ]
        } else {
            pagingAndSortingParams.sortCriteria = [
                    ["sortColumn": 'id', "sortDirection": 'asc']
            ]
        }

        return finderByAll().find([params: params, criteria: criteria], pagingAndSortingParams)
    }

    long countByCriteria(Map content, List<String> filterPropertyNames) {
        Map params = [:]
        List criteria = []
        buildCriteria(content, filterPropertyNames, params, criteria)
        return finderByAll().count([params: params, criteria: criteria])
    }

    private DynamicFinder finderByAll() {
        String query = """FROM ResourceDiagnosticMessage a"""
        return new DynamicFinder(ResourceDiagnosticMessage.class, query, "a")
    }

    private void buildCriteria(Map content, List<String> filterPropertyNames, Map params, List criteria) {
        //add custom filter criteria here
        filterPropertyNames.each { filterProperty ->
            if (content && content.get(filterProperty)) {
                params.put(filterProperty, content.get(filterProperty))
                criteria.add([key: filterProperty, binding: filterProperty, operator: Operators.EQUALS])
            }
        }
    }

}
