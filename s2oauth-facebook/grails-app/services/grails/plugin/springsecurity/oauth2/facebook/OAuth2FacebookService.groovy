package grails.plugin.springsecurity.oauth2.facebook

import com.github.scribejava.apis.FacebookApi
import com.github.scribejava.core.builder.api.DefaultApi20
import com.github.scribejava.core.model.OAuth2AccessToken
import grails.converters.JSON
import grails.plugin.springsecurity.oauth2.service.OAuth2AbstractProviderService
import grails.plugin.springsecurity.oauth2.token.OAuth2SpringToken
import grails.plugin.springsecurity.oauth2.exception.OAuth2Exception
import grails.transaction.Transactional

@Transactional
class OAuth2FacebookService extends OAuth2AbstractProviderService {

    @Override
    String getProfileScope() {
        return 'https://graph.facebook.com/me'
    }

    @Override
    String getProviderID() {
        return 'facebook'
    }

    @Override
    Class<? extends DefaultApi20> getApiClass() {
        return FacebookApi.class
    }

    @Override
    OAuth2SpringToken createSpringAuthToken(OAuth2AccessToken accessToken) {
        def response = getResponse(accessToken)
        def user
        try {
            log.debug("JSON response body: " + accessToken.rawResponse)
            user = JSON.parse(response.body)
        } catch (Exception exception) {
            log.error("Error parsing response from Facebook. Response:\n" + response.body)
            throw new OAuth2Exception("Error parsing response from Facebook", exception)
        }

        if (user?.id) {
            log.error("No user id from facebook. Response:\n" + response.body)
            throw new OAuth2Exception("No user id from facebook")
        }
    }
}
