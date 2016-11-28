/* ****************************************************************************
Copyright 2016 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.MethodFilter
import net.hedtech.restfulapi.Methods

/**
 * A method filter implementation for use with the 'restful-api' plugin.
 **/
class BannerMethodFilter extends BannerFilterConfig
                                 implements MethodFilter {


    final static CRUD_METHODS_MAP = [
        C: [Methods.CREATE],
        R: [Methods.LIST, Methods.SHOW],
        U: [Methods.UPDATE],
        D: [Methods.DELETE]
    ]


    /**
     * Dynamically determine whether a method is not allowed for a resource.
     **/
    def boolean isMethodNotAllowed(String resourceName, String methodName) {
        log.debug("Determining whether method=$methodName is not allowed for resource=$resourceName")
        def methodsNotAllowed = []
        retrieveFilterConfig(resourceName).each { config ->
            if (config.key == "*" && config.value.configActive) {
                def methodCodes = config.value.methodsNotAllowed
                for (int i = 0; i < methodCodes.length(); i++) {
                    def methodNames = CRUD_METHODS_MAP.get(methodCodes[i])
                    if (methodNames) {
                        methodsNotAllowed.addAll(methodNames)
                    }
                }
            }
        }
        boolean isNotAllowed = methodsNotAllowed.contains(methodName)
        log.debug("Determined method=$methodName is not allowed=$isNotAllowed")
        return isNotAllowed
    }
}
