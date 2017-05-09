/*******************************************************************************
 Copyright 2009-2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.integration.utility

import grails.util.Holders

/**
 * Helper class for RESTful APIs - cloned from banner_general_validation plugin.
 */
class RestfulApiValidationUtility {

    public static final Integer MAX_DEFAULT = 500
    public static final Integer MAX_UPPER_LIMIT = 1000

    /**
     * Check the values for "max" and "offset" in params map and corrects them if required.
     * Note this method updates the provided map so if that is not desired please send cloned map.
     *
     * @param params Map containing keys "max" and "offset".
     * @param maxDefault Default value to be assigned if "max" is absent.  By default it is turned off (zero).
     * @param maxUpperLimit Upper limit for "max".  By default it is turned off (zero).
     */
    public static void correctMaxAndOffset(Map params, Integer maxDefault = 0, Integer maxUpperLimit = 0) {
        // Override "max" upper limit with setting api.<resource name>.page.maxUpperLimit (if available).
        String resourceName = params?.pluralizedResourceName
        if (resourceName) {
            if (Holders.config.api."${resourceName}".page.maxUpperLimit) {
                maxUpperLimit = Holders.config.api."${resourceName}".page.maxUpperLimit
            }
        }

        if (maxUpperLimit > 0) {
            if (maxDefault < 1 || maxDefault > maxUpperLimit) {
                maxDefault = maxUpperLimit
            }
        }
        // MAX
        if (params.max) {
            if (params.max.isInteger()) {
                Integer max = params.max.toInteger()
                if (max < 1) {
                    if (maxDefault > 0) {
                        params.max = maxDefault.toString()
                    } else {
                        params.remove("max")
                    }
                } else if (maxUpperLimit > 0 && max > maxUpperLimit) {
                    params.max = maxUpperLimit.toString()
                }
            } else {
                if (maxDefault > 0) {
                    params.max = maxDefault.toString()
                } else {
                    params.remove("max")
                }
            }
        } else {
            if (maxDefault > 0) {
                params << [max: maxDefault.toString()]
            }
        }
        // OFFSET
        if (params.offset) {
            if (params.offset.isInteger()) {
                Integer offset = params.offset.toInteger()
                if (offset < 0) {
                    params.offset = "0"
                }
            } else {
                params.offset = "0"
            }
        } else {
            params << [offset: "0"]
        }
    }

}
