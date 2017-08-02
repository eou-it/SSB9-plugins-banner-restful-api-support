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
 * */

target(main: "Build hibernate patch for Ethos API extensibility feature") {

    def mavenPath = "${System.getProperty("user.home")}/.m2/repository"
    def hibernateCoreJar = "hibernate-core-3.6.10.Final.jar"
    def sourceFile = "$mavenPath/org/hibernate/hibernate-core/3.6.10.Final/$hibernateCoreJar"
    def targetFile = "lib/$hibernateCoreJar"

    def ant = new AntBuilder()

    if (!new File(targetFile).exists()) {
        println "Replacing org.hibernate.engine.query.ParameterParser class in $hibernateCoreJar"
        ant.copy(file: "$sourceFile",
                 toFile: "$targetFile",
                 overwrite: true,
                 verbose: true)
        ant.jar(destfile: "$targetFile",
                basedir: "target/classes",
                update: true,
                includes: "org/hibernate/engine/query/ParameterParser.class")
    } else {
        println "File $targetFile already exists -- skipping patch build"
    }

}

setDefaultTarget "main"
