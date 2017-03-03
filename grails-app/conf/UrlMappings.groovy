/*******************************************************************************
 Copyright 2013-2016 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

class UrlMappings {

    static mappings = {

        "/api/$pluralizedResourceName/$id"(controller: 'restfulApi') {
            action = [GET: "show", PUT: "update", DELETE: "delete"]
            parseRequest = false
        }

        "/api/$pluralizedResourceName"(controller: 'restfulApi') {
            action = [GET: "list", POST: "create"]
            parseRequest = false
        }

        "/$controller/$action?/$id?" {
            constraints {
                // apply constraints here
            }
        }

        "/"(view: "/index")
        "500"(view: '/error')

    }

}
