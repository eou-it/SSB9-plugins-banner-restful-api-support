/* ****************************************************************************
Copyright 2014-2016 Ellucian Company L.P. and its affiliates.
*******************************************************************************/

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.plugin.location.'banner-core' = '../banner_core.git'
grails.plugin.location.'restful-api' = '../restful-api.git'

grails.project.dependency.resolver = "maven"

grails.project.dependency.resolution = {

    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }

    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'

    repositories {
        grailsCentral()
    }

    dependencies {
    }

    plugins {
        //should not have to do this, but it's a bug in grails 2.2.1
        //http://jira.grails.org/browse/GRAILS-9939
        compile ":inflector:0.2"
        compile ":cache-headers:1.1.7"
    }

}
