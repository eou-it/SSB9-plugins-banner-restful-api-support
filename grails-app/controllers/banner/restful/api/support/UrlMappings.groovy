/*******************************************************************************
 Copyright 2019-2020 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

package banner.restful.api.support

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

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
