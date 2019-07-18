/* ****************************************************************************
Copyright 2013-2018 Ellucian Company L.P. and its affiliates.
******************************************************************************/
package net.hedtech.integration

import net.hedtech.integration.exception.ExceptionCollector
import net.hedtech.integration.exception.ExceptionCollectorHandler
import net.hedtech.restfulapi.ErrorResponse
import net.hedtech.restfulapi.ExceptionHandlerContext
import spock.lang.Specification


class ExceptionCollectorHandlerSpec extends Specification {


    def "Test handle nulls"() {
        setup:

        when:
        ExceptionCollectorHandler handler = new ExceptionCollectorHandler();
        ErrorResponse response = handler.handle(new ExceptionCollector(), null)


        then:
        thrown IllegalArgumentException
    }

    def "Test handle"() {
        setup:

        when:
        ExceptionCollectorHandler handler = new ExceptionCollectorHandler();
        def mock = Mock(ExceptionHandlerContext)
        ErrorResponse response = handler.handle(new ExceptionCollector(), mock)


        then:
        response != null
    }


    def "Test support"() {
        setup:

        when:
        ExceptionCollectorHandler handler = new ExceptionCollectorHandler();
        def firstCheck = handler.supports(new ExceptionCollector())
        def secondCheck = handler.supports(new RuntimeException())


        then:
        firstCheck == true
        secondCheck == false
    }


}
