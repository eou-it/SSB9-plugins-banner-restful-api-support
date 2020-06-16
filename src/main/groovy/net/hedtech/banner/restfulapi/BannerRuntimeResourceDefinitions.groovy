/*******************************************************************************
 Copyright 2020 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.restfulapi

import net.hedtech.restfulapi.RestRuntimeResourceDefinitions
import net.hedtech.restfulapi.config.JSONBeanMarshallerConfig
import net.hedtech.restfulapi.config.MarshallerConfig
import net.hedtech.restfulapi.config.RepresentationConfig
import net.hedtech.restfulapi.config.ResourceConfig

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
        if (resource.equalsIgnoreCase("custom-resources")) {
            resourceConfig = newConfigForBasketballs(resource);
        }
        if (resource.equalsIgnoreCase("soccerballs")) {
            resourceConfig = newConfigForSoccerballs(resource);
        }
        return resourceConfig;
    }
    /**
     * Fake out method to return the defintion of a basketballs resource
     * @return
     */
    ResourceConfig newConfigForBasketballs(resource) {
        ResourceConfig resourceConfig = new ResourceConfig();

        resourceConfig.name = resource
        resourceConfig.serviceName = "specDrivenApiDataModelFacadeService"
        resourceConfig.setMethods(['list', 'show'])

        JSONBeanMarshallerConfig jsonBeanMarshallerConfig = new JSONBeanMarshallerConfig();
        jsonBeanMarshallerConfig.setMarshallNullFields(false);


        RepresentationConfig representationConfigJson = new RepresentationConfig();
        representationConfigJson.setMediaType("application/json");
        representationConfigJson.setAllMediaTypes(["application/json", "application/vnd.hedtech.integration.v6+json", "application/vnd.hedtech.integration.v6.0.0+json"])

        RepresentationConfig representationConfigVersion = new RepresentationConfig();
        representationConfigVersion.setMediaType("application/vnd.hedtech.integration.v6+json");
        representationConfigVersion.setAllMediaTypes(["application/json", "application/vnd.hedtech.integration.v6+json", "application/vnd.hedtech.integration.v6.0.0+json"])

        RepresentationConfig representationConfigVersion2 = new RepresentationConfig();
        representationConfigVersion2.setMediaType("application/vnd.hedtech.integration.v6.0.0+json");
        representationConfigVersion2.setAllMediaTypes(["application/json", "application/vnd.hedtech.integration.v6+json", "application/vnd.hedtech.integration.v6.0.0+json"])


        MarshallerConfig marshallerConfig = new MarshallerConfig();
        marshallerConfig.setInstance(jsonBeanMarshallerConfig);

        representationConfigJson.setMarshallers(marshallerConfig);
        representationConfigVersion.setMarshallers(marshallerConfig);

        resourceConfig.representations.put(representationConfigVersion.getMediaType(), representationConfigVersion);
        resourceConfig.representations.put(representationConfigVersion2.getMediaType(), representationConfigVersion2);
        resourceConfig.representations.put(representationConfigJson.getMediaType(), representationConfigJson);

        return resourceConfig

    }

    ResourceConfig newConfigForSoccerballs(resource) {
        ResourceConfig resourceConfig = new ResourceConfig();

        resourceConfig.name = resource
        resourceConfig.serviceName = "runtimeResourceDefinitionCompositeService"
        resourceConfig.setMethods(['list', 'show'])

        JSONBeanMarshallerConfig jsonBeanMarshallerConfig = new JSONBeanMarshallerConfig();
        jsonBeanMarshallerConfig.setMarshallNullFields(false);

        RepresentationConfig representationConfigJson = new RepresentationConfig();
        representationConfigJson.setMediaType("application/json");
        representationConfigJson.setAllMediaTypes(["application/json", "application/vnd.hedtech.integration.v11+json"])

        RepresentationConfig representationConfigVersion = new RepresentationConfig();
        representationConfigVersion.setMediaType("application/vnd.hedtech.integration.v11+json");
        representationConfigVersion.setAllMediaTypes(["application/json", "application/vnd.hedtech.integration.v11+json"])

        MarshallerConfig marshallerConfig = new MarshallerConfig();
        marshallerConfig.setInstance(jsonBeanMarshallerConfig);

        representationConfigJson.setMarshallers(marshallerConfig);
        representationConfigVersion.setMarshallers(marshallerConfig);

        resourceConfig.representations.put(representationConfigVersion.getMediaType(), representationConfigVersion);
        resourceConfig.representations.put(representationConfigJson.getMediaType(), representationConfigJson);

        return resourceConfig
    }

    String getGenericConfigName(def acceptHeaders) {

        //"restfulapi:" + resource.name + ":" + representation.mediaType
        return ("restfulapi:restfulapiRuntimeDefinition:application/json")
    }

}