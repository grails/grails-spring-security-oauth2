package grails.plugin.springsecurity.oauth2

import com.github.scribejava.core.model.OAuth2AccessToken
import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(OAuth2TagLib)
class OAuth2TagLibSpec extends Specification {

    def SpringSecurityService springSecurityService
    def OAuth2BaseService oAuth2BaseService

    def setup() {
        springSecurityService = [:]
        oAuth2BaseService = [:]
    }

    def "ifLoggedInWith should print body if session is valid"() {
        given:
        session[oAuth2BaseService.sessionKeyForAccessToken(provider)] = new OAuth2AccessToken("${provider}_token", "${provider}_rawResponse=rawResponse")
        springSecurityService.isLoggedIn = { ->
            true
        }
        tagLib.springSecurityService = springSecurityService
        tagLib.oAuth2BaseService = oAuth2BaseService
        def template = "<s2oauth:ifLoggedInWith provider=\"${provider}\">Logged in using ${provider}</s2oauth:ifLoggedInWith>"
        and:
        def renderedContent = applyTemplate(template)
        expect:
        renderedContent == "Logged in using " + provider
        where:
        provider   | _
        'facebook' | _
        'google'   | _
        'linkedin' | _
        'twitter'  | _
    }

    def "ifLoggedInWith should print empty string if session is invalid"() {
        given:
        session[oAuth2BaseService.sessionKeyForAccessToken(provider)] = token
        springSecurityService.isLoggedIn = { ->
            true
        }
        tagLib.springSecurityService = springSecurityService
        tagLib.oAuth2BaseService = oAuth2BaseService
        def template = "<s2oauth:ifLoggedInWith provider=\"${provider}\">Logged in using ${provider}</s2oauth:ifLoggedInWith>"
        and:
        def renderedContent = applyTemplate(template)
        expect:
        renderedContent == ""
        where:
        provider   | token
        'facebook' | null
        'google'   | ""
        'linkedin' | "a_token_string"
    }

    def "ifLoggedInWith should print empty string if user is not logged in"() {
        given:
        springSecurityService.isLoggedIn = { ->
            return false
        }
        tagLib.springSecurityService = springSecurityService
        def template = '<s2oauth:ifLoggedInWith provider="unknown">Logged in using unknown provider</s2oauth:ifLoggedInWith>'
        when:
        def renderedContent = applyTemplate(template)
        then:
        renderedContent == ''
    }

    def "ifNotLoggedInWith should print body if user is not logged in"() {
        given:
        def message = "Not_Logged_In"
        springSecurityService.isLoggedIn = { ->
            return false
        }
        tagLib.springSecurityService = springSecurityService
        def template = "<s2oauth:ifNotLoggedInWith provider=\"facebook\">${message}</s2oauth:ifNotLoggedInWith>"
        when:
        def renderedContent = applyTemplate(template)
        then:
        renderedContent == message
    }
}
