package grails.plugin.springsecurity.oauth2

import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.builder.api.DefaultApi20
import com.github.scribejava.core.model.OAuth2AccessToken
import com.github.scribejava.core.oauth.OAuth20Service

/**
 * Always code as if the guy who ends up maintaining your code 
 * will be a violent psychopath who knows where you live. 
 * Code for readability.
 *
 * John F. Woods
 *
 * Created by Johannes on 06.04.2016.
 */
abstract class OAuth2ProviderService {

    private OAuth20Service authService

    private OAuth2ProviderConfiguration providerConfiguration

    /**
     * @return The ProviderID
     */
    abstract def String getProviderID()

    /**
     * @return The ApiClass that is to use
     */
    abstract Class<? extends DefaultApi20> getApiClass()

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
        buildScribeService(oAuth2ProviderConfiguration)
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
     */
    private void buildScribeService(OAuth2ProviderConfiguration providerConfiguration) {
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

        authService = serviceBuilder.build(getApiClass())
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
}