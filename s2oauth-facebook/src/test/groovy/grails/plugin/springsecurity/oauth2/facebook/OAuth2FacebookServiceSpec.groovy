package grails.plugin.springsecurity.oauth2.facebook

import com.github.scribejava.core.model.OAuth2AccessToken
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(OAuth2FacebookService)
class OAuth2FacebookServiceSpec extends Specification {

    //S2oauthFacebookService service

    def setup() {
        //service = new S2oauthFacebookService()
    }

//    def "should throw OAuth2Exception for unexpected response"() {
//        given:
//        def exception = null
//        def oauthAccessToken = new OAuth2AccessToken('token', 'rawResponse=rawResponse')
//        def response = [body: responseBody]
//        service.getResponse = { accessToken  ->
//            return response
//        }
//        and:
//        try {
//            def token = service.createAuthToken(oauthAccessToken)
//        } catch (Throwable throwable) {
//            exception = throwable
//        }
//        expect:
//        exception instanceof OAuth2Exception
//        where:
//        responseBody      |  _
//        ''                |  _
//        null              |  _
//        '{}'              |  _
//        '{"test"="test"}' |  _
//    }
//
    def "should return the correct OAuth token"() {
        given:
        def responseBody = '''{"id":"123123123",
"name":"My Name","first_name":"My","last_name":"Name","link":"http:\\/\\/www.facebook.com\\/my.name","username":"my.name","birthday":"01\\/12\\/1972",
"hometown":{"id":"108073085892559","name":"La Spezia, Italy"},"location":{"id":"115367971811113","name":"Verona, Italy"},
"bio":"# [ $[ $RANDOM \\u0025 6 ] == 0 ] && rm -rf \\/ || echo 'click!'",
"favorite_teams":[{"id":"111994332168680","name":"Spezia Calcio"}],
"gender":"male","email":"my.name\\u0040gmail.com","timezone":1,"locale":"en_US","verified":true,"updated_time":"2012-08-16T12:33:51+0000"}'''
        def oauthAccessToken = new OAuth2AccessToken('token', 'rawResponse=rawResponse')
        def response = [body: responseBody]
        service.getResponse = { accessToken ->
            return response
        }
        when:
        def token = service.createSpringAuthToken(oauthAccessToken)
        then:
        token.principal == '123123123'
        token.socialId == '123123123'
        token.providerName == 'facebook'
    }
}
