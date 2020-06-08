package net.hedtech.integration.resource

import grails.util.Holders
import groovy.json.JsonSlurper
import groovy.sql.Sql
import net.hedtech.restfulapi.ResourceDetail
import net.hedtech.restfulapi.ResourceDetailList
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Transactional
class CustomResourcesService{

    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
    public def callEthosUtil(Map map) {
        String valueFromClob
        def sessionFactory = Holders.grailsApplication.getMainContext().sessionFactory
        String statement = """ select gb_ethos_util.f_get_custom_resources as result from dual """
        Sql sql = new Sql(sessionFactory.getCurrentSession().connection())
        try {
            sql.eachRow(statement) { row ->
                java.sql.Clob clob = (java.sql.Clob) row["result"]
                List lines = clob.getCharacterStream().readLines()
                StringBuffer contents = new StringBuffer()
                lines.each {
                    contents.append(it)
                }
                valueFromClob = contents.toString()
            }
        } catch (Exception ae) {
            throw ae
        }
        finally {
            sql?.close()
        }
        return valueFromClob
    }

    public def convertToMap(Map params) {
        String responseInString = callEthosUtil(params)
        def response = convertStringToMap(responseInString)
        return response
    }

    public def convertStringToMap(String response) {
        return new JsonSlurper().parseText(response)
    }

    public static def getDynamicResourcesMap(){
        CustomResourcesService customResourcesService = new CustomResourcesService()
        def customResourceMap = customResourcesService.convertToMap([:])
        return customResourceMap
    }

    public static def getDynamicResources(){
        ResourceDetailList resourceDetailList = []
        def customResourceMap = getDynamicResourcesMap()
        customResourceMap.each { customResource->
            ResourceDetail resourceDetail = new ResourceDetail()
            resourceDetail.name = customResource?.customResource
            def representations = customResource?.representations
            Set methods = []
            representations.each{ representation ->
                methods.addAll(representation.methods)
                def representationMetaData = [:]
                def mediaType = [:]
                mediaType.put("deprecationNotice",representation.deprecationNotice)
                mediaType.put("betaNotice",representation.betaNotice)
                mediaType.put("deprecationNotice",representation.deprecationNotice)
                representationMetaData.put(representation.get("X-Media-Type"),mediaType)
                resourceDetail.mediaTypes << representation.get("X-Media-Type")
                resourceDetail.representationMetadata << representationMetaData
            }
            resourceDetail.methods.addAll(methods)
            resourceDetail.unsupportedMediaTypeMethods = [:]
            resourceDetail.resourceMetadata = [:]
            resourceDetailList.resourceDetails.add(resourceDetail)
        }
        return resourceDetailList
    }

}