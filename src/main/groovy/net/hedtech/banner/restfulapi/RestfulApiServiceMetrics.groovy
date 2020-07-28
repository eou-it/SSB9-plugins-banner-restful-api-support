/* ****************************************************************************
Copyright 2014-2020 Ellucian Company L.P. and its affiliates.
******************************************************************************/

package net.hedtech.banner.restfulapi

import groovy.time.TimeCategory 
import groovy.time.TimeDuration

import org.apache.commons.logging.LogFactory
import groovy.util.logging.Slf4j
@ Slf4j
class RestfulApiServiceMetrics {

    private static final HashMap metricsMap = new HashMap()
    
//    private static final log = LogFactory.getLog(this)
    
    private static trackApiMetrics = false


    /**
     * Logs metrics accumulating invocation count with total, min, and max elapsed times.
     * @params service service to be associated with the metric for accumulations
     * @params params parameters used to determine resource name for service
     * @params operation service operation that was performed
     * @params startDate start date
     * @params endDate end date
     **/
    public static void logMetrics(def service, Map params, String operation, Date startDate, Date endDate, def resultSize = null) {
        if (!log.isTraceEnabled() && !trackApiMetrics) return
        try {
            def metrics = null
            def metricName = generateMetricName(service, params, operation)
            def elapsedTime = endDate.time - startDate.time // capture elapsed time in milliseconds
            synchronized (metricsMap) {
                def metricsList = metricsMap.get(metricName)
                if (metricsList == null) {
                    metricsList = [0, 0, 0, 9999999999, 0, null] // [count, totalSize, totalTime, minTime, maxTime, lastActivity]
                    metricsMap.put(metricName, metricsList)
                }
                // increment count
                metricsList[0]++
                // add result size to total size
                metricsList[1] += (resultSize ?: 0)
                // add elapsed time to total time
                metricsList[2] += elapsedTime
                // adjust min time if needed
                if (elapsedTime < metricsList[3]) {
                    metricsList[3] = elapsedTime
                }
                // adjust max time if needed
                if (elapsedTime > metricsList[4]) {
                    metricsList[4] = elapsedTime
                }
                // record current time for last activity
                metricsList[5] = new Date()
                // create a clone to be used in the log statement outside of the synchronized block
                metrics = metricsList.clone()
            }
            // log metrics only if trace is enabled
            if (log.isTraceEnabled()) {
                // calulate duration
                TimeDuration duration = TimeCategory.minus(endDate, startDate)
                if (operation == "list") {
                    log.trace("${metricName} time:${duration} (size=${(resultSize ?: 0)}) [count:${metrics[0]}, totalSize:${metrics[1]}, totalTime:${metrics[2]}, minTime:${metrics[3]}, maxTime:${metrics[4]}])")
                } else {
                    log.trace("${metricName} time:${duration} [count:${metrics[0]}, totalTime:${metrics[2]}, minTime:${metrics[3]}, maxTime:${metrics[4]}])")
                }
            }
        } catch (Throwable t) {
            log.trace(t)
        }
    }


    /**
     * Clears all metrics.
     */
    public static void clearAllMeterics() {
        synchronized (metricsMap) {
            metricsMap.clear()
        }
    }
    
    
    /**
     * Generate metric name based on service and operation.
     */
    private static generateMetricName(def service, Map params, String operation) {
        def metricName = service.class.simpleName
        if (params.pluralizedResourceName != null) {
            metricName = params.pluralizedResourceName
            if (params.parentPluralizedResourceName != null) {
                metricName = params.parentPluralizedResourceName + "." + metricName
            }
        }
        def index = metricName.indexOf('$')
        if (index != -1) {
            metricName = metricName.substring(0, index)
        }
        return "${metricName}.${operation}"
    }


    /**
     * Returns a clone of the metrics map. Tracking of API metrics must be enabled.
     */
    public static Map getAllMeterics() {
        def clonedMetricsMap = new HashMap()
        if (trackApiMetrics) {
            synchronized (metricsMap) {
                metricsMap.each {
                    clonedMetricsMap.put(it.key, it.value.clone())
                }
            }
        }
        return clonedMetricsMap
    }


    /**
     * Allowing tracking of API metrics for non-logging purposes.
     */
    public static void setTrackApiMetrics(boolean trackApiMetrics) {
        this.trackApiMetrics = trackApiMetrics
    }

}
