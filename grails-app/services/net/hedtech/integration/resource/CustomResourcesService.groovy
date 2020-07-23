package net.hedtech.integration.resource
import grails.util.Holders
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import net.hedtech.restfulapi.Methods
import net.hedtech.restfulapi.ResourceDetail
import net.hedtech.restfulapi.ResourceDetailList
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
@Transactional
class CustomResourcesService {

    SupportedResourceService supportedResourceService


    public def callEthosUtil() {
        String nativeSql = """select GURASPC_RESOURCE,GURASPC_RETURNED_MEDIA_TYPE,GURASPC_KNOWN_MEDIA_TYPES,GURASPC_METHODS,GURASPC_UNSUPPORTED_METHODS from GURASPC"""
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

    public static def getDynamicResources() {
        ResourceDetailList resourceDetailList = []
        String depricationNotice
        CustomResourcesService customResourcesService = new CustomResourcesService()
        def rows = customResourcesService.callEthosUtil()
        Collection<String> uniqueResourceNames = rows?.GURASPC_RESOURCE?.unique()
        uniqueResourceNames.each {
            ResourceDetail resourceDetail = new ResourceDetail()
            resourceDetail.name = it
            Collection<GroovyRowResult> results = rows?.findAll { entity -> entity?.GURASPC_RESOURCE == it }
            def representationsList = []
            List<String> methods = []
            Map unsupportedMediaTypeMethods = [:]
            results?.each { result ->
                def unsupportedMethodsList = []
                def representationListForEachRecord = []
                String knownMediaTypes = result[2]
                String supportedMethods = result[3]
                String unsupportedMethods = result[4]
                if(knownMediaTypes){
                    representationsList.addAll(knownMediaTypes?.split(','))
                    representationListForEachRecord?.addAll(knownMediaTypes?.split(','))
                }
                if(supportedMethods){
                    methods.addAll(supportedMethods?.split(','))
                }
                if(unsupportedMethods){
                    unsupportedMethodsList.addAll(unsupportedMethods?.split(','))
                }

                representationListForEachRecord?.each {rep->
                    unsupportedMediaTypeMethods.put(rep,unsupportedMethodsList?.unique())
                }

                depricationNotice = result[5]
            }

            resourceDetail.unsupportedMediaTypeMethods = unsupportedMediaTypeMethods
            representationsList.each { representation ->
                Map representationMetaData = [:]
                resourceDetail.representationMetadata << representationMetaData
                resourceDetail.mediaTypes << representation
                resourceDetail.methods = methods?.unique()
            }
            resourceDetailList?.resourceDetails?.add(resourceDetail)
        }
        return resourceDetailList
    }
}