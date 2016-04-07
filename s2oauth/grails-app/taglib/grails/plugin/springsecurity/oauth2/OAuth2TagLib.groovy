package grails.plugin.springsecurity.oauth2

import com.github.scribejava.core.model.OAuth2AccessToken
import grails.plugin.springsecurity.SpringSecurityService
import org.grails.taglib.GrailsTagException

class OAuth2TagLib {

    static namespace = "oauth2"

    def OAuth2BaseService oAuth2BaseService
    def SpringSecurityService springSecurityService

    /**
     * Creates a link to connect to the give provider.
     */
    def connect = { attrs, body ->
        String provider = attrs.provider
        if (!provider) {
            throw new GrailsTagException('No provider specified for <oauth:connect /> tag. Try <oauth2:connect provider="your-provider-name" />')
        }
        Map a = attrs + [url: [controller: 'OAuth2', action: 'authenticate', params: [provider: provider]]]
        out << g.link(a, body)
    }

    /**
     * Renders the body if the user is authenticated with the given provider.
     */
    def ifLoggedInWith = { attrs, body ->
        String provider = attrs.provider
        if (currentUserIsLoggedInWithProvider(provider)) {
            out << body()
        }
    }

    /**
     * Renders the body if the user is not authenticated with the given provider.
     */
    def ifNotLoggedInWith = { attrs, body ->
        String provider = attrs.provider
        if (!currentUserIsLoggedInWithProvider(provider)) {
            out << body()
        }
    }

    private boolean currentUserIsLoggedInWithProvider(String provider) {
        if (!provider || !springSecurityService.isLoggedIn()) {
            return false
        }
        def sessionKey = oAuth2BaseService.sessionKeyForAccessToken(provider)
        return (session[sessionKey] instanceof OAuth2AccessToken)
    }
}
