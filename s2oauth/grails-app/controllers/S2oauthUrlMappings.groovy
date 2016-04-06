class S2oauthUrlMappings {

    static mappings = {
        //TODO Clean
//        "/oauth/$provider/callback"(controller: 's2oauth', action: 'callback')
//        "/oauth/$provider/authenticate"(controller: 's2oauth', action: 'authenticate')
//        "/oauth/$provider/success"(controller: 's2oauth', action: 'onSuccess')
//        "/oauth/$provider/failure"(controller: 's2oauth', action: 'onFailure')

        "oauth2/$provider/callback"(controller: 'OAuth2', action: 'callback')
        "oauth2/$provider/failure"(controller: 'OAuth2', action: 'onFailure')

        '/oauth2/account/ask'(controller: 'OAuth2', action: 'ask')
        '/oauth2/account/link'(controller: 'OAuth2', action: 'linkAccount')
        '/oauth2/account/create'(controller: 'OAuth2', action: 'createAccount')
    }
}
