package grails.plugin.springsecurity.oauth2

import com.github.scribejava.core.model.OAuth2AccessToken
import grails.plugin.springsecurity.oauth2.exception.OAuth2Exception
import grails.plugin.springsecurity.oauth2.token.OAuth2SpringToken
import grails.plugin.springsecurity.userdetails.GrailsUser
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(OAuth2Controller)
class OAuth2ControllerSpec extends Specification {

    @Unroll
    def "Registration command objects for #loginId validating correctly"() {

        given: "a mocked command object"
        def urc = mockCommandObject(OAuth2CreateAccountCommand)
        urc.oAuth2BaseService = [existUserNamed: { u -> false }]

        and: "a set of initial values from the spock test"
        urc.username = loginId
        urc.password1 = password
        urc.password2 = passwordRepeat

        when: "the validator is invoked"
        def isValidRegistration = urc.validate()

        then: "the appropriate fields are flagged as errors"
        isValidRegistration == anticipatedValid
        urc.errors.getFieldError(fieldInError)?.code == errorCode

        where:
        loginId | password   | passwordRepeat | anticipatedValid | fieldInError | errorCode
        "glen"  | "password" | "no-match"     | false            | "password2"  | "OAuthCreateAccountCommand.password.error.mismatch"
        "glen"  | "!QAZxsw"  | "!QAZxsw"      | false            | "password1"  | "minSize.notmet"
        "glen"  | "password" | "password"     | false            | "password1"  | "OAuthCreateAccountCommand.password.error.strength"
        "peter" | "!QAZxsw2" | "!QAZxsw2"     | true             | null         | null
        "a"     | "password" | "password"     | false            | "username"   | "minSize.notmet"

    }

    def "authenticate should throw exception if request is missing provider"() {
        given:
        params.provider = provider
        def oAuth2BaseService = [
                sessionKeyForRequestToken: { p -> 'no-such-key-in-session' },
                getAuthorizationUrl: { p, t -> 'url' },
        ]
        controller.oAuth2BaseService = oAuth2BaseService
        when:
        controller.authenticate()
        then:
        thrown OAuth2Exception
        where:
        provider      |  _
        ''            |  _
        '  '          |  _
        null          |  _
    }

    def "onSuccess should throw exception if request is missing data"() {
        given:
        params.provider = provider
        def oAuth2BaseService = [ sessionKeyForAccessToken:{ p -> 'no-such-key-in-session' } ]
        controller.oAuth2BaseService = oAuth2BaseService
        when:
        controller.onSuccess()
        then:
        thrown OAuth2Exception
        where:
        provider      |  _
        ''            |  _
        null          |  _
        'facebook'    |  _
    }

    // now askToLinkOrCreateAccountUri is hardcoded, so this test is redundant
    def "onSuccess should throw exception if askToLinkOrCreateAccountUri is not set"() {
        given:
        OAuth2SpringToken authToken = Mock()
        params.provider = provider
        def providerkey = "${provider}_oauth_session_key"
        session[providerkey] = "${provider}_oauth_session_key"
        def oAuth2BaseService = [
                sessionKeyForAccessToken:{ p -> providerkey },
                createAuthToken: { p, t -> authToken },
                getAskToLinkOrCreateAccountUri: { null }
        ]
        controller.oAuth2BaseService = oAuth2BaseService
        when:
        controller.onSuccess()
        then:
        thrown OAuth2Exception
        where:
        provider      |  _
        'facebook'    |  _
    }

    def "onSuccess should redirect to askToLinkOrCreateAccountUri if the user is not logged in"() {
        given:
        OAuth2SpringToken authToken = Mock(OAuth2SpringToken)
        params.provider = provider
        def providerkey = "${provider}_oauth_session_key"
        session[providerkey] = "${provider}aaa"
        def oAuth2BaseService = [
                sessionKeyForAccessToken:{ p -> providerkey },
                createAuthToken: { p, t -> authToken },
                getAskToLinkOrCreateAccountUri: { "/askToLinkOrCreateAccountUri" }
        ]
        controller.oAuth2BaseService = oAuth2BaseService
        and:
        controller.onSuccess()
        expect:
        response.status == responseCode
        response.redirectedUrl == "/askToLinkOrCreateAccountUri"
        where:
        provider      |  responseCode
        'facebook'    |  302
    }

    def "onSuccess should redirect to defaultTargeturl if user is logged in"() {
        given:
        def token = Stub(OAuth2AccessToken) {
            getRawResponse() >> "a=1&b=2"
        }
        OAuth2SpringToken authToken = new TestOAuth2SpringToken(token, false)
        params.provider = provider
        def providerkey = "${provider}_oauth_session_key"
        session[providerkey] = "${provider}aaa"
        def oAuth2BaseService = [
                sessionKeyForAccessToken:{ p -> providerkey },
                createAuthToken: { p, t -> authToken },
                getAskToLinkOrCreateAccountUri: { "/ask" }
        ]
        controller.oAuth2BaseService = oAuth2BaseService
        and:
        controller.onSuccess()
        expect:
        response.status == responseCode
        response.redirectedUrl == controller.getDefaultTargetUrl().uri
        where:
        provider      |  responseCode
        'facebook'    |  302
    }

    def "chooseAccount should return view if user is not logged in"() {
        given:
        controller.springSecurityService = [ isLoggedIn: { false } ]
        when:
        def mav = controller.linkAccount()
        then:
        mav.viewName == '/s2oauth/chooseaccount'
    }
}

/**
 * A basic implementation for oauth token for a loggedin user.
 */
class TestOAuth2SpringToken extends OAuth2SpringToken {
    TestOAuth2SpringToken(OAuth2AccessToken token, boolean json) {
        super(token, json)
        this.principal = new GrailsUser("username", "password", true, true, true, true, [], 1L)
    }
    String getProviderName() {
        return "provider"
    }
    String getSocialId() {
        return "socialId"
    }
    String getScreenName() {
        return "screenName"
    }
}
