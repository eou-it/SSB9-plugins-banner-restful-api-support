/* ****************************************************************************
Copyright 2013-2016 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.banner.restfulapi

import net.hedtech.banner.service.ServiceBase

import spock.lang.*

class RestfulApiServiceBaseAdapterSpec extends Specification {

    def "Test threadlocal params on list"() {
        setup:
        def mock = Mock(ServiceBase)
        def threadLocalParams
        mock.list(_) >> {
            threadLocalParams = RestfulApiRequestParams.get()
            []
        }
        def adapter = new RestfulApiServiceBaseAdapter()
        def params = ['one':'two']

        when:
        adapter.list(mock, params)

        then:
        ['one':'two'] == threadLocalParams
        [:] == RestfulApiRequestParams.get()
    }

    def "Test threadlocal params on count"() {
        setup:
        def mock = Mock(ServiceBase)
        def threadLocalParams
        mock.count(_) >> {
            threadLocalParams = RestfulApiRequestParams.get()
            []
        }
        def adapter = new RestfulApiServiceBaseAdapter()
        def params = ['one':'two']

        when:
        adapter.count(mock, params)

        then:
        ['one':'two'] == threadLocalParams
        [:] == RestfulApiRequestParams.get()
    }

    def "Test threadlocal params on show"() {
        setup:
        def mock = Mock(ServiceBase)
        def threadLocalParams
        mock.get(_) >> {
            threadLocalParams = RestfulApiRequestParams.get()
            []
        }
        def adapter = new RestfulApiServiceBaseAdapter()
        def params = ['one':'two']

        when:
        adapter.show(mock, params)

        then:
        ['one':'two'] == threadLocalParams
        [:] == RestfulApiRequestParams.get()
    }

    def "Test threadlocal params on create"() {
        setup:
        def mock = Mock(ServiceBase)
        def threadLocalParams
        mock.create(_) >> {
            threadLocalParams = RestfulApiRequestParams.get()
            []
        }
        def adapter = new RestfulApiServiceBaseAdapter()
        def params = ['one':'two']

        when:
        adapter.create(mock, [:], params)

        then:
        ['one':'two'] == threadLocalParams
        [:] == RestfulApiRequestParams.get()
    }

    def "Test threadlocal params on update"() {
        setup:
        def mock = Mock(ServiceBase)
        def threadLocalParams
        mock.update(_) >> {
            threadLocalParams = RestfulApiRequestParams.get()
            []
        }
        def adapter = new RestfulApiServiceBaseAdapter()
        def params = ['one':'two']

        when:
        adapter.update(mock, [:], params)

        then:
        ['one':'two'] == threadLocalParams
        [:] == RestfulApiRequestParams.get()
    }

    def "Test threadlocal params on delete"() {
        setup:
        def mock = Mock(ServiceBase)
        def threadLocalParams
        mock.delete(_) >> {
            threadLocalParams = RestfulApiRequestParams.get()
            []
        }
        def adapter = new RestfulApiServiceBaseAdapter()
        def params = ['one':'two']

        when:
        adapter.delete(mock, [:], params)

        then:
        ['one':'two'] == threadLocalParams
        [:] == RestfulApiRequestParams.get()
    }

}
