/* ****************************************************************************
Copyright 2014 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.banner.restfulapi

import groovy.time.TimeCategory 
import groovy.time.TimeDuration

import org.apache.commons.logging.LogFactory

class RestfulApiServiceMetrics {

    private static final HashMap metricsMap = new HashMap()
    
    private static final log = LogFactory.getLog(this)


    /**
     * Logs metrics accumulating invocation count with total, min, and max elapsed times.
     * @params service service to be associated with the metric for accumulations
     * @params operation service operation that was performed
     * @params startDate start date
     * @params endDate end date
     **/
    public static void logMetrics(def service, String operation, Date startDate, Date endDate) {
        if (!log.isTraceEnabled()) return
        try {
            def metrics = null
            def metricName = generateMetricName(service, operation)
            def elapsedTime = endDate.time - startDate.time // capture elapsed time in milliseconds
            synchronized (metricsMap) {
                def metricsList = metricsMap.get(metricName)
                if (metricsList == null) {
                    metricsList = [0, 0, 9999999999, 0] // [count, totalTime, minTime, maxTime]
                    metricsMap.put(metricName, metricsList)
                }
                // increment count
                metricsList[0]++
                // add elapsed time to total time
                metricsList[1] += elapsedTime
                // adjust min time if needed
                if (elapsedTime < metricsList[2]) {
                    metricsList[2] = elapsedTime
                }
                // adjust max time if needed
                if (elapsedTime > metricsList[3]) {
                    metricsList[3] = elapsedTime
                }
                // create a clone to be used in the log statement outside of the synchronized block
                metrics = metricsList.clone()
            }
            // calulate duration
            TimeDuration duration = TimeCategory.minus(endDate, startDate)
            log.trace("${metricName} time:${duration} [count:${metrics[0]}, totalTime:${metrics[1]}, minTime:${metrics[2]}, maxTime:${metrics[3]}])")
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
    private static generateMetricName(def service, String operation) {
        def metricName = service.class.simpleName
        def index = metricName.indexOf('$')
        if (index != -1) {
            metricName = metricName.substring(0, index)
        }
        return "${metricName}.${operation}"
    }

}
