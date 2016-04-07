package grails.plugin.springsecurity.oauth2.dummy

import com.github.scribejava.core.builder.api.DefaultApi20
import com.github.scribejava.core.model.OAuth2AccessToken
import grails.plugin.springsecurity.oauth2.dummy.token.DummyOAuth2SpringToken
import grails.plugin.springsecurity.oauth2.service.OAuth2AbstractProviderService
import grails.plugin.springsecurity.oauth2.token.OAuth2SpringToken
import grails.transaction.Transactional

@Transactional
class OAuth2DummyService extends OAuth2AbstractProviderService {
    /**
     * @return The ProviderID
     */
    @Override
    String getProviderID() {
        return "dummy"
    }

    /**
     * A scribeJava API class to use for the oAuth Request or any other class that extends the @link{DefaultApi20}* @return The ApiClass that is to use
     */
    @Override
    Class<? extends DefaultApi20> getApiClass() {
        return DummyScribeApi.class
    }

    /**
     * Path to the OAuthScope that is returning the UserIdentifier
     * i.e 'https://graph.facebook.com/me' for facebook
     */
    @Override
    String getProfileScope() {
        return 'http://localhost:8080/oauth/dummy/callback?code=dummycode'
    }

    /**
     * @param accessToken
     * @return
     */
    @Override
    OAuth2SpringToken createSpringAuthToken(OAuth2AccessToken accessToken) {
        OAuth2AccessToken token = new OAuth2AccessToken(DUMMY_KEY, DUMMY_RAW_RESPONSE)
        return new DummyOAuth2SpringToken(token, 'user.id')
    }
}
