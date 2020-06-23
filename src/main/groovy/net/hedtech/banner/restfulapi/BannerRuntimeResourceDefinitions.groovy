/*******************************************************************************
 Copyright 2020 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.restfulapi

import grails.util.Holders
import net.hedtech.restfulapi.RestRuntimeResourceDefinitions
import net.hedtech.restfulapi.apiversioning.BasicApiVersionParser
import net.hedtech.restfulapi.config.JSONBeanMarshallerConfig
import net.hedtech.restfulapi.config.MarshallerConfig
import net.hedtech.restfulapi.config.RepresentationConfig
import net.hedtech.restfulapi.config.ResourceConfig
import org.springframework.transaction.annotation.Transactional
import groovy.sql.Sql


@Transactional
import groovy.util.logging.Slf4j

@Slf4j
class BannerRuntimeResourceDefinitions implements RestRuntimeResourceDefinitions {

    /**
     * This function will be the entry point.
     *
     * Input is a resource name, output is a resource config.
     *
     * If a custom resource is found, adapt it to a ResourceConfg
     *
     * If a custom resource is not found return null
     *
     * This has to cache any DB or work...because this is called serveral times during a request thread
     *
     * @param resource
     * @return
     */
    ResourceConfig getResourceConfig(String resource) {
        ResourceConfig resourceConfig = null;
        if (resource) {
            resourceConfig = newConfigForCustomResources(resource);
        }
        return resourceConfig;
    }
    /**
     * Fake out method to return the defintion of a basketballs resource
     * @return
     */
    ResourceConfig newConfigForCustomResources(resource) {
        ResourceConfig resourceConfig = new ResourceConfig();

        def results = executeNativeSQL(resource)

        resourceConfig.name = resource
        resourceConfig.serviceName = "specDrivenAPIDataModelFacadeService"
        resourceConfig.setMethods(['list', 'show'])


        JSONBeanMarshallerConfig jsonBeanMarshallerConfig = new JSONBeanMarshallerConfig();
        jsonBeanMarshallerConfig.setMarshallNullFields(false);
        results?.each {
            String sampleText = it[2]
            Collection<String> list = sampleText.split(',')
            String returnedMediaType = it[1]
            list?.each {
                RepresentationConfig representationConfigJson = new RepresentationConfig();
                representationConfigJson.setMediaType(it)
                representationConfigJson.setAllMediaTypes(list)

                MarshallerConfig marshallerConfig = new MarshallerConfig();
                marshallerConfig.setInstance(jsonBeanMarshallerConfig);

                representationConfigJson.setMarshallers(marshallerConfig);

                def apiVersion = new BasicApiVersionParser().parseMediaType(resource,returnedMediaType)
                representationConfigJson.setApiVersion(apiVersion)

                resourceConfig.representations.put(representationConfigJson.getMediaType(), representationConfigJson);
            }
        }

        return resourceConfig

    }

    String getGenericConfigName(def acceptHeaders) {
        //"restfulapi:" + resource.name + ":" + representation.mediaType
        return ("restfulapi:restfulapiRuntimeDefinition:application/json")
    }

    private def executeNativeSQL(String resourceName) {
        String nativeSql = """select GURASPC_RESOURCE,GURASPC_RETURNED_MEDIA_TYPE,GURASPC_KNOWN_MEDIA_TYPES from GURASPC where GURASPC_RESOURCE = '$resourceName'"""
        def rows
        def sql
        def sessionFactory = Holders?.grailsApplication?.getMainContext()?.sessionFactory
        try {
            sql = new Sql(sessionFactory.getCurrentSession().connection())
            rows = sql.rows(nativeSql)
        } catch (Exception e) {
            System.out.println(e)
        }
        log.debug "Executed native SQL successfully"
        return rows
    }

}