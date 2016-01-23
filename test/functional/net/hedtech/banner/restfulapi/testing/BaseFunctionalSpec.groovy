package net.hedtech.banner.restfulapi.testing

import net.hedtech.restfulapi.spock.RestSpecification
import org.codehaus.groovy.grails.plugins.codecs.Base64Codec

abstract class BaseFunctionalSpec extends RestSpecification {

    final String RESTFUL_API_BASE_URL = 'http://localhost:' + System.getProperty('server.port', '8080') + '/' + grails.util.Metadata.current.getApplicationName() + '/api'


    protected String authHeader(username = "grails_user", password = "u_pick_it") {
        def authString = Base64Codec.encode("$username:$password")
        System.out.print(authString)
        "Basic ${authString}" as String
    }

}
