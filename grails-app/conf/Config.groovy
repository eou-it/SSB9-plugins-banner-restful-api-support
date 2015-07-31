/*******************************************************************************
 Copyright 2013-2015 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
import net.hedtech.banner.configuration.ApplicationConfigurationUtils as ConfigFinder

println "appName -> ${appName}"

grails.config.locations = [] // leave this initialized to an empty list, and add your locations in the map below.

def locationAdder = ConfigFinder.&addLocation.curry(grails.config.locations)

[ BANNER_APP_CONFIG:        "banner_configuration.groovy",
  BANNER_CORE_TESTAPP_CONFIG: "banner_core_testapp_configuration.groovy",
].each { envName, defaultFileName -> locationAdder( envName, defaultFileName ) }

grails.config.locations.each {
    println "config location -> " + it
}
