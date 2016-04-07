class OAuth2UrlMappings {

    static mappings = {
        "oauth2/$provider/callback"(controller: 'OAuth2', action: 'callback')
        "oauth2/$provider/failure"(controller: 'OAuth2', action: 'onFailure')

        '/oauth2/account/ask'(controller: 'OAuth2', action: 'ask')
        '/oauth2/account/link'(controller: 'OAuth2', action: 'linkAccount')
        '/oauth2/account/create'(controller: 'OAuth2', action: 'createAccount')
    }
}
