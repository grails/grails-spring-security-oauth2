package grails.plugin.springsecurity.oauth2.service

import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.builder.api.DefaultApi20
import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Response
import com.github.scribejava.core.model.Verb
import com.github.scribejava.core.oauth.OAuth20Service
import grails.plugin.springsecurity.oauth2.util.OAuth2ProviderConfiguration
import grails.plugin.springsecurity.oauth2.token.OAuth2SpringToken

/**
 * Always code as if the guy who ends up maintaining your code 
 * will be a violent psychopath who knows where you live. 
 * Code for readability.
 *
 * John F. Woods
 *
 * Created by Johannes on 06.04.2016.
 */
abstract class OAuth2AbstractProviderService implements OAuth2ProviderService {

    private OAuth20Service authService

    private OAuth2ProviderConfiguration providerConfiguration

    /**
     * @return The ProviderID
     */
    abstract String getProviderID()

    /**
     * A scribeJava API class to use for the oAuth Request or any other class that extends the @link{DefaultApi20}* @return The ApiClass that is to use
     */
    abstract Class<? extends DefaultApi20> getApiClass()

    /**
     * Path to the OAuthScope that is returning the UserIdentifier
     * i.e 'https://graph.facebook.com/me' for facebook
     */
    abstract String getProfileScope()

    /**
     * @param accessToken
     * @return
     */
    abstract OAuth2SpringToken createSpringAuthToken(OAuth2AccessToken accessToken)

    /**
     * Initialize the service with a configuration
     * @param oAuth2ProviderConfiguration
     */
    void init(OAuth2ProviderConfiguration oAuth2ProviderConfiguration) {
        providerConfiguration = oAuth2ProviderConfiguration
        authService = buildScribeService(oAuth2ProviderConfiguration)
    }

    /**
     * Get the access token from the oAuth2 Service
     * @param authCode
     * @return
     */
    OAuth2AccessToken getAccessToken(String authCode) {
        authService.getAccessToken(authCode)
    }

    /**
     * Get the authorization URL
     * @param params Additional params for the url call
     * @return
     */
    String getAuthUrl(Map<String, String> params) {
        authService.getAuthorizationUrl(params)
    }

    /**
     * Create the scribe service to make oAuthCalls with
     * @param providerConfiguration
     * @return a scribejava service builder
     */
    ServiceBuilder buildScribeService(OAuth2ProviderConfiguration providerConfiguration) {
        final String secretState = getProviderID() + "-secret-" + new Random().nextInt(999_999)
        ServiceBuilder serviceBuilder = new ServiceBuilder()
                .apiKey(providerConfiguration.apiKey)
                .apiSecret(providerConfiguration.apiSecret)
                .state(secretState)
        if (providerConfiguration.callbackUrl) {
            serviceBuilder.callback(providerConfiguration.callbackUrl)
        }
        if (providerConfiguration.scope) {
            serviceBuilder.scope(providerConfiguration.scope)
        }
        if (providerConfiguration.debug) {
            serviceBuilder.debug()
        }

        serviceBuilder.build(getApiClass())
    }

    /**
     * @return The configured successUrl
     */
    String getSuccessUrl() {
        providerConfiguration.successUrl
    }

    /**
     * @return The configure failureUrl
     */
    String getFailureUrl() {
        providerConfiguration.failureUrl
    }

    /**
     * Get the response from OAuthServer
     * @param profileUrl
     * @param accessToken
     * @return
     */
    Response getResponse(OAuth2AccessToken accessToken) {
        OAuthRequest oAuthRequest = new OAuthRequest(Verb.GET, getProfileScope(), authService)
        authService.signRequest(accessToken, oAuthRequest);
        oAuthRequest.send()
    }
}