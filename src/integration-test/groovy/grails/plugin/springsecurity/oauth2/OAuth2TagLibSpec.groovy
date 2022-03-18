package grails.plugin.springsecurity.oauth2

import com.github.scribejava.core.model.OAuth2AccessToken
import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.mixin.integration.Integration
import grails.testing.web.taglib.TagLibUnitTest
import spock.lang.Specification

/**
 * Always code as if the guy who ends up maintaining your code
 * will be a violent psychopath that knows where you live.
 *
 * - John Woods
 *
 * Created on 21.06.2016
 * @author MatrixCrawler
 */
@Integration
class OAuth2TagLibSpec extends Specification implements TagLibUnitTest<OAuth2TagLib>{

    void "ifLoggedInWith should print body if session is valid"() {
        given:
        def springSecurityService = Mock(SpringSecurityService)
        springSecurityService.isLoggedIn() >> {true}
        def springSecurityOauth2BaseService = Mock(SpringSecurityOauth2BaseService)
        springSecurityOauth2BaseService.sessionKeyForAccessToken(provider) >> {'OAuth2: access - t:' + provider}
        session[springSecurityOauth2BaseService.sessionKeyForAccessToken(provider)] = new OAuth2AccessToken("${provider}_token", "${provider}_rawResponse=rawResponse")

        tagLib.springSecurityService = springSecurityService
        tagLib.springSecurityOauth2BaseService = springSecurityOauth2BaseService
        def template = "<oauth2:ifLoggedInWith provider=\"${provider}\">Logged in using ${provider}</oauth2:ifLoggedInWith>"
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

    void "ifLoggedInWith should print empty string if session is invalid"() {
        given:
        def springSecurityService = Mock(SpringSecurityService)
        springSecurityService.isLoggedIn() >> {true}
        def springSecurityOauth2BaseService = Mock(SpringSecurityOauth2BaseService)
        springSecurityOauth2BaseService.sessionKeyForAccessToken(provider) >> {'OAuth2: access - t:' + provider}
        session[springSecurityOauth2BaseService.sessionKeyForAccessToken(provider)] = token
        tagLib.springSecurityService = springSecurityService
        tagLib.springSecurityOauth2BaseService = springSecurityOauth2BaseService
        def template = "<oauth2:ifLoggedInWith provider=\"${provider}\">Logged in using ${provider}</oauth2:ifLoggedInWith>"
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

    void "ifLoggedInWith should print empty string if user is not logged in"() {
        given:
        def springSecurityService = Mock(SpringSecurityService)
        springSecurityService.isLoggedIn() >> {false}
        tagLib.springSecurityService = springSecurityService
        def template = '<oauth2:ifLoggedInWith provider="unknown">Logged in using unknown provider</oauth2:ifLoggedInWith>'
        when:
        def renderedContent = applyTemplate(template)
        then:
        renderedContent == ''
    }

    void "ifNotLoggedInWith should print body if user is not logged in"() {
        given:
        def message = "Not_Logged_In"
        def springSecurityService = Mock(SpringSecurityService)
        springSecurityService.isLoggedIn() >> {false}
        tagLib.springSecurityService = springSecurityService
        def template = "<oauth2:ifNotLoggedInWith provider=\"facebook\">${message}</oauth2:ifNotLoggedInWith>"
        when:
        def renderedContent = applyTemplate(template)
        then:
        renderedContent == message
    }
}
