package grails.plugin.springsecurity.oauth2.service

import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.builder.api.DefaultApi20
import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.model.Response
import grails.plugin.springsecurity.oauth2.util.OAuth2ProviderConfiguration
import grails.plugin.springsecurity.oauth2.token.OAuth2SpringToken

/**
 * Always code as if the guy who ends up maintaining your code
 * will be a violent psychopath that knows where you live.
 *
 * - John Woods
 *
 * Created on 07.04.2016.
 * @author J.Brunswicker
 */
interface OAuth2ProviderService {

    /**
     * @return The ProviderID
     */
    String getProviderID()

    /**
     * A scribeJava API class to use for the oAuth Request or any other class that extends the @link{DefaultApi20}* @return The ApiClass that is to use
     */
    Class<? extends DefaultApi20> getApiClass()

    /**
     * Path to the OAuthScope that is returning the UserIdentifier
     * i.e 'https://graph.facebook.com/me' for facebook
     */
    String getProfileScope()

    /**
     * @param accessToken
     * @return
     */
    OAuth2SpringToken createSpringAuthToken(OAuth2AccessToken accessToken)

    /**
     * Initialize the service with a configuration
     * @param oAuth2ProviderConfiguration
     */
    void init(OAuth2ProviderConfiguration oAuth2ProviderConfiguration)

    /**
     * Get the access token from the oAuth2 Service
     * @param authCode
     * @return
     */
    OAuth2AccessToken getAccessToken(String authCode)

    /**
     * Get the authorization URL
     * @param params Additional params for the url call
     * @return
     */
    String getAuthUrl(Map<String, String> params)

    /**
     * Create the scribe service to make oAuthCalls with
     * @param providerConfiguration
     */
    ServiceBuilder buildScribeService(OAuth2ProviderConfiguration providerConfiguration)

    /**
     * @return The configured successUrl
     */
    String getSuccessUrl()

    /**
     * @return The configure failureUrl
     */
    String getFailureUrl()

    /**
     * Get the response from OAuthServer
     * @param profileUrl
     * @param accessToken
     * @return
     */
    Response getResponse(OAuth2AccessToken accessToken)

}