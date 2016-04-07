package grails.plugin.springsecurity.oauth2.util
/**
 * Always code as if the guy who ends up maintaining your code 
 * will be a violent psychopath who knows where you live. 
 * Code for readability.
 *
 * John F. Woods
 *
 * Created by Johannes on 06.04.2016.
 */

/**
 * Helper class to store the configuration values for the OAuth2Provider
 */
class OAuth2ProviderConfiguration {

    String apiKey

    String apiSecret

    String callbackUrl

    String scope

    boolean debug = false

    String successUrl

    String failureUrl
}
