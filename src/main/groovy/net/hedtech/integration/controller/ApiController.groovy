/*******************************************************************************
 Copyright 2014-2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.controller

import grails.validation.ValidationException
import grails.databinding.SimpleMapDataBindingSource
import net.hedtech.banner.exceptions.ApplicationException

/**
 * Base class use by api controllers, provides some general services
 */
class ApiController {

    def grailsWebDataBinder

    private static List globalBindExcludes = ['id', 'version', 'dataOrigin']

    /**
     * Used to bind map properties onto grails domains.
     * Can provide an exclusion and inclusion list in the third param.
     */
    public void bindData(
            def domain, Map properties, Map includeExcludeMap, boolean checkForErrors = true, boolean globalBinds = true) {
        if (globalBinds) {
            if (includeExcludeMap?.exclude instanceof List) {
                includeExcludeMap.exclude.addAll(globalBindExcludes)
            } else {
                includeExcludeMap.put('exclude', globalBindExcludes)
            }
        }
        grailsWebDataBinder.bind(domain,
                properties as SimpleMapDataBindingSource,
                null,
                includeExcludeMap?.include,
                includeExcludeMap?.exclude,
                null)
        if (checkForErrors && domain.hasErrors()) {
            throw new ApplicationException("${domain.class.simpleName}", new ValidationException("${domain.class.simpleName}", domain.errors))
        }
    }
}
