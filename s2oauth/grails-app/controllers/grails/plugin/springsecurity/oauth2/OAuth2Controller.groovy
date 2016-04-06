package grails.plugin.springsecurity.oauth2

import com.github.scribejava.core.model.OAuth2AccessToken
import com.sun.istack.internal.Nullable
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.userdetails.GrailsUser
import org.apache.commons.lang.exception.ExceptionUtils
import grails.plugin.springsecurity.oauth2.exception.OAuth2Exception
import org.springframework.security.core.context.SecurityContextHolder

class OAuth2Controller {

    public static final String SPRING_SECURITY_OAUTH_TOKEN = 'springSecurityOAuthToken'

    def oAuth2BaseService

    def callback() {
        String providerName = params.provider
        log.debug("Callback for " + providerName)

        // Check if we got an AuthCode from the server query
        String authCode = params.code
        log.debug("AuthCode: " + authCode)
        if (!authCode || authCode.isEmpty()) {
            throw new OAuth2Exception("No AuthCode in callback for provider '${providerName}'")
        }

        def providerService = oAuth2BaseService.getProviderService(providerName)
        OAuth2AccessToken accessToken
        try {
            accessToken = providerService.getAccessToken(authCode)
        } catch (IllegalArgumentException exception) {
            log.error("Could not authenticate with oAuth2. " + ExceptionUtils.getMessage(exception))
            log.debug(ExceptionUtils.getStackTrace(exception))
            redirect(uri: oAuth2BaseService.getFailureUrl(providerName))
            return
        }
        session[oAuth2BaseService.sessionKeyForAccessToken(providerName)] = accessToken
        redirect(uri: oAuth2BaseService.getSuccessUrl(providerName))
    }

    def onFailure(String provider) {
        flash.error = "Error authenticating with ${provider}"
        log.warn("Error authentication with OAuth2Provider ${provider}")
        authenticateAndRedirect(null, getDefaultTargetUrl())
    }

    def onSuccess(String provider) {
        if (!provider) {
            log.warn "The Spring Security OAuth callback URL must include the 'provider' URL parameter"
            throw new OAuth2Exception("The Spring Security OAuth callback URL must include the 'provider' URL parameter")
        }
        def sessionKey = oAuth2BaseService.sessionKeyForAccessToken(provider)
        if (!session[sessionKey]) {
            log.warn "No OAuth token in the session for provider '${provider}'"
            throw new OAuth2Exception("Authentication error for provider '${provider}'")
        }
        // Create the relevant authentication token and attempt to log in.
        OAuth2SpringToken oAuthToken = oAuth2BaseService.createAuthToken(provider, session[sessionKey])

        if (oAuthToken.principal instanceof GrailsUser) {
            authenticateAndRedirect(oAuthToken, getDefaultTargetUrl())
        } else {
            // This OAuth account hasn't been registered against an internal
            // account yet. Give the oAuthID the opportunity to create a new
            // internal account or link to an existing one.
            session[SPRING_SECURITY_OAUTH_TOKEN] = oAuthToken

            def redirectUrl = oAuth2BaseService.getAskToLinkOrCreateAccountUri()
            if (!redirectUrl) {
                log.warn "grails.plugin.springsecurity.oauth.registration.askToLinkOrCreateAccountUri configuration option must be set"
                throw new OAuth2Exception('Internal error')
            }
            log.debug "Redirecting to askToLinkOrCreateAccountUri: ${redirectUrl}"
            redirect(redirectUrl instanceof Map ? redirectUrl : [uri: redirectUrl])
        }
    }

    def ask() {
        //TODO Implement
    }

    def linkAccount() {
        //TODO Implement
    }

    def createAccount() {
        //TODO Implement
    }

    /**
     * Set authentication token and redirect to the page we came from
     * @param oAuthToken
     * @param redirectUrl
     */
    protected void authenticateAndRedirect(@Nullable OAuth2SpringToken oAuthToken, redirectUrl) {
        session.removeAttribute SPRING_SECURITY_OAUTH_TOKEN
        SecurityContextHolder.context.authentication = oAuthToken
        redirect(redirectUrl instanceof Map ? redirectUrl : [uri: redirectUrl])
    }

    /**
     * Get default Url
     * @return
     */
    protected Map getDefaultTargetUrl() {
        def config = SpringSecurityUtils.securityConfig
        def savedRequest = SpringSecurityUtils.getSavedRequest(session)
        def defaultUrlOnNull = '/'
        if (savedRequest && !config.successHandler.alwaysUseDefault) {
            return [url: (savedRequest.redirectUrl ?: defaultUrlOnNull)]
        }
        return [uri: (config.successHandler.defaultTargetUrl ?: defaultUrlOnNull)]
    }
}
