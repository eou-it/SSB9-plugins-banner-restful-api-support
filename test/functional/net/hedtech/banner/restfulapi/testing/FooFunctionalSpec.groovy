/* ****************************************************************************
Copyright 2013-2016 Ellucian Company L.P. and its affiliates.
******************************************************************************/

package net.hedtech.banner.restfulapi.testing

import net.hedtech.banner.testing.Foo

import grails.converters.JSON

import groovy.sql.Sql

class FooFunctionalSpec extends BaseFunctionalSpec {

    private static final String RESOURCE_NAME = "foo"


    void setup() {
        cleanupTestData()
        setupTestData()
    }


    void cleanup() {
        cleanupTestData()
    }


    def "list - read-write"() {
        when:
        get("${RESTFUL_API_BASE_URL}/${RESOURCE_NAME}") {
            headers['Accept'] = 'application/vnd.hedtech.integration.v1+json'
            headers['Authorization'] = authHeader()
        }

        then:
        200 == response.status
        'application/json' == response.contentType
        'application/vnd.hedtech.integration.v1+json' == responseHeader("X-hedtech-Media-Type")
        "List of foo resources" == responseHeader('X-hedtech-message')
        def numberOfRecordsInResponse = JSON.parse(response.text).size()
        0 < numberOfRecordsInResponse
        0 == responseHeader('X-hedtech-pageOffset').toInteger()
    }


    def "create - read-write"() {
        when:
        post("${RESTFUL_API_BASE_URL}/${RESOURCE_NAME}") {
            headers['Content-Type'] = 'application/json'
            headers['Accept'] = 'application/vnd.hedtech.integration.v1+json'
            headers['Authorization'] = authHeader()
            body {
                """
				{
                    "code":"TT",
                    "description":"Test"
				}
                """
            }
        }

        then:
        201 == response.status
        'application/json' == response.contentType
        'application/vnd.hedtech.integration.v1+json' == responseHeader("X-hedtech-Media-Type")
        "foo resource created" == responseHeader('X-hedtech-message')
        def foo = JSON.parse(response.text)
        "TT" == foo.code
        "Test" == foo.description
    }


    def "update - read-write"() {
        setup:
        int id
        Foo.withTransaction {
            def sql = new Sql(getSessionFactory().currentSession.connection())
            sql.execute(INSERT_STVCOLL, ['TT','Test'])
            id = sql.firstRow(SELECT_STVCOLL_SURROGATE_ID, ['TT']).stvcoll_surrogate_id.intValue()
        }

        when:
        put("${RESTFUL_API_BASE_URL}/${RESOURCE_NAME}/${id}") {
            headers['Content-Type'] = 'application/json'
            headers['Accept'] = 'application/vnd.hedtech.integration.v1+json'
            headers['Authorization'] = authHeader()
            body {
                """
				{
                    "id":"$id",
                    "version":0,
                    "code":"TT",
                    "description":"Test Update"
				}
                """
            }
        }

        then:
        200 == response.status
        'application/json' == response.contentType
        'application/vnd.hedtech.integration.v1+json' == responseHeader("X-hedtech-Media-Type")
        "foo resource updated" == responseHeader('X-hedtech-message')
        def foo = JSON.parse(response.text)
        "TT" == foo.code
        "Test Update" == foo.description
    }


    def "delete - read-write"() {
        setup:
        int id
        Foo.withTransaction {
            def sql = new Sql(getSessionFactory().currentSession.connection())
            sql.execute(INSERT_STVCOLL, ['TT','Test'])
            id = sql.firstRow(SELECT_STVCOLL_SURROGATE_ID, ['TT']).stvcoll_surrogate_id.intValue()
        }

        when:
        delete("${RESTFUL_API_BASE_URL}/${RESOURCE_NAME}/${id}") {
            headers['Content-Type'] = 'application/json'
            headers['Accept'] = 'application/vnd.hedtech.integration.v1+json'
            headers['Authorization'] = authHeader()
        }

        then:
        200 == response.status
        "foo resource deleted" == responseHeader('X-hedtech-message')
    }


    def "list - read-only"() {
        when:
        get("${RESTFUL_API_BASE_URL}/${RESOURCE_NAME}") {
            headers['Accept'] = 'application/vnd.hedtech.integration.v1+json'
            headers['Authorization'] = readOnlyAuthHeader()
        }

        then:
        200 == response.status
        'application/json' == response.contentType
        'application/vnd.hedtech.integration.v1+json' == responseHeader("X-hedtech-Media-Type")
        "List of foo resources" == responseHeader('X-hedtech-message')
        def numberOfRecordsInResponse = JSON.parse(response.text).size()
        0 < numberOfRecordsInResponse
        0 == responseHeader('X-hedtech-pageOffset').toInteger()
    }


    def "create - read-only"() {
        when:
        post("${RESTFUL_API_BASE_URL}/${RESOURCE_NAME}") {
            headers['Content-Type'] = 'application/json'
            headers['Accept'] = 'application/vnd.hedtech.integration.v1+json'
            headers['Authorization'] = readOnlyAuthHeader()
            body {
                """
				{
					"code":"TT",
					"description":"Test"
				}
                """
            }
        }

        then:
        405 == response.status
        'application/json' == response.contentType
        "Method not allowed." == responseHeader('X-hedtech-message')
    }


    def "update - read-only"() {
        setup:
        int id
        Foo.withTransaction {
            def sql = new Sql(getSessionFactory().currentSession.connection())
            sql.execute(INSERT_STVCOLL, ['TT','Test'])
            id = sql.firstRow(SELECT_STVCOLL_SURROGATE_ID, ['TT']).stvcoll_surrogate_id.intValue()
        }

        when:
        put("${RESTFUL_API_BASE_URL}/${RESOURCE_NAME}/${id}") {
            headers['Content-Type'] = 'application/json'
            headers['Accept'] = 'application/vnd.hedtech.integration.v1+json'
            headers['Authorization'] = readOnlyAuthHeader()
            body {
                """
				{
                    "id":"$id",
                    "version":0,
                    "code":"TT",
                    "description":"Test Update"
				}
                """
            }
        }

        then:
        405 == response.status
        'application/json' == response.contentType
        "Method not allowed." == responseHeader('X-hedtech-message')
    }


    def "delete - read-only"() {
        setup:
        int id
        Foo.withTransaction {
            def sql = new Sql(getSessionFactory().currentSession.connection())
            sql.execute(INSERT_STVCOLL, ['TT','Test'])
            id = sql.firstRow(SELECT_STVCOLL_SURROGATE_ID, ['TT']).stvcoll_surrogate_id.intValue()
        }

        when:
        delete("${RESTFUL_API_BASE_URL}/${RESOURCE_NAME}/${id}") {
            headers['Content-Type'] = 'application/json'
            headers['Accept'] = 'application/vnd.hedtech.integration.v1+json'
            headers['Authorization'] = readOnlyAuthHeader()
        }

        then:
        405 == response.status
        'application/json' == response.contentType
        "Method not allowed." == responseHeader('X-hedtech-message')
    }


    def "create - read-write supersedes read-only"() {
        setup:
        Foo.withTransaction {
            def sql = new Sql(getSessionFactory().currentSession.connection())
            sql.execute(INSERT_GURUCLS, ['BAN_FULL_CRUD_FOO_C', 'GRAILS_USER_READONLY'])
        }

        when:
        post("${RESTFUL_API_BASE_URL}/${RESOURCE_NAME}") {
            headers['Content-Type'] = 'application/json'
            headers['Accept'] = 'application/vnd.hedtech.integration.v1+json'
            headers['Authorization'] = readOnlyAuthHeader()
            body {
                """
				{
					"code":"TT",
					"description":"Test"
				}
                """
            }
        }

        then:
        201 == response.status
        'application/json' == response.contentType
        'application/vnd.hedtech.integration.v1+json' == responseHeader("X-hedtech-Media-Type")
        "foo resource created" == responseHeader('X-hedtech-message')
        def foo = JSON.parse(response.text)
        "TT" == foo.code
        "Test" == foo.description
    }


    private void setupTestData() {
        Foo.withTransaction {
            def sql = new Sql(getSessionFactory().currentSession.connection())

            // setup security objects
            sql.execute(INSERT_GUBOBJS, ['API_TEST_FOO_SERVICE_API', 'Test Foo Service API', 'G'])

            // setup read-write user
            sql.execute(INSERT_GTVCLAS, ['BAN_FULL_CRUD_FOO_C'])
            sql.execute(INSERT_GURUCLS, ['BAN_FULL_CRUD_FOO_C', 'GRAILS_USER'])
            sql.execute(INSERT_GURUOBJ, ['BAN_FULL_CRUD_FOO_C', 'API_TEST_FOO_SERVICE_API', 'BAN_DEFAULT_M'])

            // setup read-only user
            sql.execute(INSERT_GTVCLAS, ['BAN_READ_ONLY_FOO_C'])
            sql.execute(INSERT_GURUCLS, ['BAN_READ_ONLY_FOO_C', 'GRAILS_USER_READONLY'])
            sql.execute(INSERT_GURUOBJ, ['BAN_READ_ONLY_FOO_C', 'API_TEST_FOO_SERVICE_API', 'BAN_DEFAULT_Q'])
        }
    }


    private void cleanupTestData() {
        Foo.withTransaction {
            def sql = new Sql(getSessionFactory().currentSession.connection())

            // cleanup data inserted by the test
            sql.execute(DELETE_STVCOLL, ['TT'])

            // cleanup read-write user
            sql.execute(DELETE_GURUOBJ, ['BAN_FULL_CRUD_FOO_C'])
            sql.execute(DELETE_GURUCLS, ['BAN_FULL_CRUD_FOO_C'])
            sql.execute(DELETE_GTVCLAS, ['BAN_FULL_CRUD_FOO_C'])

            // cleanup read-only user
            sql.execute(DELETE_GURUOBJ, ['BAN_READ_ONLY_FOO_C'])
            sql.execute(DELETE_GURUCLS, ['BAN_READ_ONLY_FOO_C'])
            sql.execute(DELETE_GTVCLAS, ['BAN_READ_ONLY_FOO_C'])

            // cleanup security objects
            sql.execute(DELETE_GUBOBJS, ['API_TEST_FOO_SERVICE_API'])
        }
    }


    def INSERT_GUBOBJS = """
insert into gubobjs (gubobjs_name,gubobjs_desc,gubobjs_objt_code,gubobjs_sysi_code,gubobjs_user_id,gubobjs_activity_date,gubobjs_help_ind,gubobjs_extract_enabled_ind,gubobjs_ui_version)
values (?,?,'FORM',?,'BASELINE',SYSDATE,'N','N','A')"""
    def INSERT_GTVCLAS = """
insert into bansecr.gtvclas (gtvclas_class_code,gtvclas_sysi_code,gtvclas_data_origin,gtvclas_user_id,gtvclas_activity_date)
values (?,'G','BASELINE',USER,SYSDATE)"""
    def INSERT_GURUCLS = """
insert into bansecr.gurucls (gurucls_class_code,gurucls_userid,gurucls_user_id,gurucls_activity_date)
values (?,?,USER,SYSDATE)"""
    def INSERT_GURUOBJ = """
insert into bansecr.guruobj (guruobj_userid,guruobj_object,guruobj_role,guruobj_data_origin,guruobj_user_id,guruobj_activity_date)
values (?,?,?,'BASELINE',USER,SYSDATE)"""

    def DELETE_GURUOBJ = """delete from bansecr.guruobj where guruobj_userid = ?"""
    def DELETE_GURUCLS = """delete from bansecr.gurucls where gurucls_class_code = ?"""
    def DELETE_GTVCLAS = """delete from bansecr.gtvclas where gtvclas_class_code = ?"""
    def DELETE_GUBOBJS = """delete from gubobjs where gubobjs_name = ?"""

    def INSERT_STVCOLL = """
insert into stvcoll (stvcoll_code,stvcoll_desc,stvcoll_data_origin,stvcoll_user_id,stvcoll_activity_date)
values (?,?,'GRAILS_USER',USER,SYSDATE)"""
    def SELECT_STVCOLL_SURROGATE_ID = """select stvcoll_surrogate_id from stvcoll where stvcoll_code = ?"""
    def DELETE_STVCOLL = """delete from stvcoll where stvcoll_code = ?"""

}
