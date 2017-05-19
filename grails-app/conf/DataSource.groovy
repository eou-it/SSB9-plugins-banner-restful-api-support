/*******************************************************************************
 Copyright 2015 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsAnnotationConfiguration

dataSource {
    configClass = GrailsAnnotationConfiguration.class
    dialect = "org.hibernate.dialect.Oracle10gDialect"
    loggingSql = false

}

hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
    hbm2ddl.auto = null
    show_sql = false
    //naming_strategy = "org.hibernate.cfg.ImprovedNamingStrategy"
    dialect = "org.hibernate.dialect.Oracle10gDialect"
    config.location = ["classpath:hibernate-banner-core.cfg.xml",
                       "classpath:hibernate-banner-core.testing.cfg.xml",
                       "classpath:hibernate-banner-general-common.cfg.xml",
                       "classpath:hibernate-banner-general-validation-common.cfg.xml",
                       "classpath:hibernate-banner-restful-api-support.cfg.xml"]
}

// environment specific settings
environments {
    development {
        dataSource {
        }
    }
    test {
        dataSource {
        }
    }
    production {
        dataSource {
        }
    }
}
