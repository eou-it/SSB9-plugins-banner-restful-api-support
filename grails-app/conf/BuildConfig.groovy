grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.plugin.location.'banner-core' = '../banner_core.git'

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsCentral()
    }
    dependencies {
        //should not have to do this, but it's a bug in grails 2.2.1
        //http://jira.grails.org/browse/GRAILS-9939
        test "org.spockframework:spock-grails-support:0.7-groovy-2.0"
    }

    plugins {
        compile ":spring-security-core:1.2.7.3"
        //should not have to do this, but it's a bug in grails 2.2.1
        //http://jira.grails.org/browse/GRAILS-9939
        compile ":inflector:0.2"
        compile ":restful-api:0.8.0"
        build(":tomcat:7.0.52.1",
              ":release:2.2.0",
              ":rest-client-builder:1.0.3") {
            export = false
        }
    }
}
