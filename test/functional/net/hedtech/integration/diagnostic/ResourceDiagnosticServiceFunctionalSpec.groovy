/* ****************************************************************************
Copyright 2017 Ellucian Company L.P. and its affiliates.
******************************************************************************/

package net.hedtech.integration.diagnostic

import grails.converters.JSON
import java.util.concurrent.TimeUnit
import net.hedtech.banner.restfulapi.testing.BaseFunctionalSpec

class ResourceDiagnosticServiceFunctionalSpec extends BaseFunctionalSpec {

    private static final String RESOURCE_NAME = "diagnostics"

    def "list diagnostic messages"() {
        setup:
        deleteAllResourceDiagnosticMessages()
        createResourceDiagnosticMessage(1, "test-resource-1", "INFO", "first message")
        createResourceDiagnosticMessage(2, "test-resource-2", "WARN", "second message")
        createResourceDiagnosticMessage(3, "test-resource-3", "ERROR", "third message")
        3 == countResourceDiagnosticMessages()

        when:
        get("${RESTFUL_API_BASE_URL}/${RESOURCE_NAME}") {
            headers['Accept'] = 'application/json'
            headers['Authorization'] = authHeader()
        }

        then:
        200 == response.status
        'application/json' == response.contentType
        'application/json' == responseHeader("X-hedtech-Media-Type")
        "List of diagnostic resources" == responseHeader('X-hedtech-message')
        "3" == responseHeader('X-hedtech-totalCount')
        def resources = JSON.parse(response.text)
        3 == resources.size()
        def resource1 = resources.get(0)
        1 == resource1.id
        "test-resource-1" == resource1.resourceName
        "INFO" == resource1.messageLevel
        "first message" == resource1.message
        null != resource1.lastModified
        def resource2 = resources.get(1)
        2 == resource2.id
        "test-resource-2" == resource2.resourceName
        "WARN" == resource2.messageLevel
        "second message" == resource2.message
        null != resource2.lastModified
        def resource3 = resources.get(2)
        3 == resource3.id
        "test-resource-3" == resource3.resourceName
        "ERROR" == resource3.messageLevel
        "third message" == resource3.message
        null != resource3.lastModified
    }

    def "list diagnostic messages with paging"() {
        setup:
        deleteAllResourceDiagnosticMessages()
        createResourceDiagnosticMessage(1, "test-resource-1", "INFO", "first message")
        createResourceDiagnosticMessage(2, "test-resource-2", "WARN", "second message")
        createResourceDiagnosticMessage(3, "test-resource-3", "ERROR", "third message")
        createResourceDiagnosticMessage(4, "test-resource-4", "ERROR", "fourth message")
        4 == countResourceDiagnosticMessages()

        when:
        get("${RESTFUL_API_BASE_URL}/${RESOURCE_NAME}?offset=1&max=2") {
            headers['Accept'] = 'application/json'
            headers['Authorization'] = authHeader()
        }

        then:
        200 == response.status
        'application/json' == response.contentType
        'application/json' == responseHeader("X-hedtech-Media-Type")
        "List of diagnostic resources" == responseHeader('X-hedtech-message')
        "4" == responseHeader('X-hedtech-totalCount')
        def resources = JSON.parse(response.text)
        2 == resources.size()
        def resource1 = resources.get(0)
        2 == resource1.id
        "test-resource-2" == resource1.resourceName
        "WARN" == resource1.messageLevel
        "second message" == resource1.message
        null != resource1.lastModified
        def resource2 = resources.get(1)
        3 == resource2.id
        "test-resource-3" == resource2.resourceName
        "ERROR" == resource2.messageLevel
        "third message" == resource2.message
        null != resource2.lastModified
    }

    def "submit diagnostic messages job"() {
        setup:
        deleteAllResourceDiagnosticMessages()
        0 == countResourceDiagnosticMessages()

        when:
        post("${RESTFUL_API_BASE_URL}/${RESOURCE_NAME}") {
            headers['Content-Type'] = 'application/json'
            headers['Authorization'] = authHeader()
        }

        then:
        201 == response.status
        'text/plain' == response.contentType
        null == responseHeader("X-hedtech-Media-Type")
        "diagnostic resource created" == responseHeader('X-hedtech-message')
        "Created" == response.text

        // pause 10 times for 1 second each waiting for data to be created in asynchronous job
        def list
        def finished = false
        for (def i : (1..10)) {
            TimeUnit.MILLISECONDS.sleep(1000)
            list = findAllResourceDiagnosticMessages()
            if (list.find { it.messageLevel == "FINISHED" }) {
                finished = true
                break
            }
        }
        true == finished
        2 <= list.size()
        "STARTED" == list.get(0).messageLevel
        "FINISHED" == list.get(list.size()-1).messageLevel

    }

    private void deleteAllResourceDiagnosticMessages() {
        ResourceDiagnosticMessage.withTransaction {
            ResourceDiagnosticMessage.findAll().each { resourceDiagnosticMessage ->
                resourceDiagnosticMessage.delete(flush: true, failOnError: true)
            }
        }
    }

    private int countResourceDiagnosticMessages() {
        def cnt
        ResourceDiagnosticMessage.withTransaction {
            cnt = ResourceDiagnosticMessage.count()
        }
        return cnt
    }

    private List<ResourceDiagnosticMessage> findAllResourceDiagnosticMessages() {
        def list
        ResourceDiagnosticMessage.withTransaction {
            list = ResourceDiagnosticMessage.findAll([sort: "id"])
        }
        return list
    }

    private void createResourceDiagnosticMessage(Long id, String resourceName, String messageLevel, String message) {
        ResourceDiagnosticMessage resourceDiagnosticMessage = new ResourceDiagnosticMessage()
        resourceDiagnosticMessage.id = id
        resourceDiagnosticMessage.resourceName = resourceName
        resourceDiagnosticMessage.messageLevel = messageLevel
        resourceDiagnosticMessage.message = message
        resourceDiagnosticMessage.lastModified = new Date()
        ResourceDiagnosticMessage.withTransaction {
            resourceDiagnosticMessage.save(flush: true, failOnError: true)
        }
    }
}
