/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
/** *****************************************************************************
FOR DEVELOPMENT USE ONLY
****************************************************************************** */

/**
 * Build hibernate patch for Ethos API extensibility feature.
 * Usage: 'grails build-hibernate-patch'
 * The lib/hibernate-core-3.6.10.Final.jar must be manually deleted before running this csript.
 *
 * This patch resolves the following problems for extensibility 'WRITE' operations:
 * - org.hibernate.QueryException: Space is not allowed after parameter prefix ':'
 * - see: https://hibernate.atlassian.net/browse/HHH-2697
 * - java.lang.IllegalArgumentException: Invalid filter-parameter name format
 * - see: https://github.com/hibernate/hibernate-orm/pull/804/files
 * - requires patched version of ParameterParser and QueryParameters source code (for 3.6.10-Final)
 * */

target(main: "Build hibernate patch for Ethos API extensibility feature") {

    def mavenPath = "${System.getProperty("user.home")}/.m2/repository"
    def hibernateCoreJar = "hibernate-core-3.6.10.Final.jar"
    def sourceFile = "$mavenPath/org/hibernate/hibernate-core/3.6.10.Final/$hibernateCoreJar"
    def targetFile = "lib/$hibernateCoreJar"

    def ant = new AntBuilder()

    if (!new File(targetFile).exists()) {
        println "Replacing org.hibernate.engine.query.ParameterParser"
        println "and org.hibernate.engine.QueryParameters classes in"
        println "$hibernateCoreJar"
        ant.copy(file: "$sourceFile",
                 toFile: "$targetFile",
                 overwrite: true,
                 verbose: true)
        ant.jar(destfile: "$targetFile",
                basedir: "target/classes",
                update: true,
                includes: "org/hibernate/engine/query/ParameterParser.class,org/hibernate/engine/QueryParameters.class")
    } else {
        println "File $targetFile already exists -- skipping patch build"
    }

}

setDefaultTarget "main"
