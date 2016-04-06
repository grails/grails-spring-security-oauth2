package grails.plugin.springsecurity.oauth2

import com.github.scribejava.core.model.OAuth2AccessToken
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.userdetails.GormUserDetailsService
import grails.plugin.springsecurity.userdetails.GrailsUser
import grails.plugin.springsecurity.oauth2.exception.OAuth2Exception
import org.springframework.security.core.authority.SimpleGrantedAuthority

class OAuth2BaseService {

    /**
     * Map for storing the different OAuth2Provider
     */
    Map<String, OAuth2ProviderService> providerServiceMap = new HashMap<>()

    def grailsApplication

    OAuth2SpringToken createAuthToken(String providerName, OAuth2AccessToken scribeToken) {
        def providerService = getProviderService(providerName)
        OAuth2SpringToken oAuthToken = providerService.createSpringAuthToken(scribeToken)
        def OAuthID = lookupOAuthIdClass()
        def oAuthID = OAuthID.findByProviderAndAccessToken(oAuthToken.providerName, oAuthToken.socialId)
        if (oAuthID) {
            updateOAuthToken(oAuthToken, oAuthID.user)
        }
        return oAuthToken
    }

    OAuth2SpringToken updateOAuthToken(OAuth2SpringToken oAuthToken, user) {
        def conf = SpringSecurityUtils.securityConfig

        // user

        String usernamePropertyName = conf.userLookup.usernamePropertyName
        String passwordPropertyName = conf.userLookup.passwordPropertyName
        String enabledPropertyName = conf.userLookup.enabledPropertyName
        String accountExpiredPropertyName = conf.userLookup.accountExpiredPropertyName
        String accountLockedPropertyName = conf.userLookup.accountLockedPropertyName
        String passwordExpiredPropertyName = conf.userLookup.passwordExpiredPropertyName

        String username = user."${usernamePropertyName}"
        String password = user."${passwordPropertyName}"
        boolean enabled = enabledPropertyName ? user."${enabledPropertyName}" : true
        boolean accountExpired = accountExpiredPropertyName ? user."${accountExpiredPropertyName}" : false
        boolean accountLocked = accountLockedPropertyName ? user."${accountLockedPropertyName}" : false
        boolean passwordExpired = passwordExpiredPropertyName ? user."${passwordExpiredPropertyName}" : false

        // authorities

        String authoritiesPropertyName = conf.userLookup.authoritiesPropertyName
        String authorityPropertyName = conf.authority.nameField
        Collection<?> userAuthorities = user."${authoritiesPropertyName}"
        def authorities = userAuthorities.collect { new SimpleGrantedAuthority(it."${authorityPropertyName}") }

        oAuthToken.principal = new GrailsUser(username, password, enabled, !accountExpired, !passwordExpired,
                !accountLocked, authorities ?: [GormUserDetailsService.NO_ROLE], user.id)
        oAuthToken.authorities = authorities
        oAuthToken.authenticated = true

        return oAuthToken
    }

    /**
     * Register the provider into the service
     * @param providerService
     */
    def void registerProvider(OAuth2ProviderService providerService) {
        log.debug("Registering provider: " + providerService.providerID)
        if (providerServiceMap.containsKey(providerService.providerID)) {
            // There is already a provider under that name
            log.warn("There is already a provider with the name " + providerService.providerID + " registered")
        } else {
            String baseURL = getBaseUrl()
            def callbackURL = baseURL + "/oauth2/" + providerService.providerID + "/callback"
            log.debug("Callback URL: " + callbackURL)
            def successUrl = grailsApplication.config.getProperty("oauth2.providers.${providerService.providerID}.success") ? baseURL + grailsApplication.config.getProperty("oauth2.providers.${providerService.providerID}.success") : null
            log.debug("Success URL: " + successUrl)
            def failureUrl = grailsApplication.config.getProperty("oauth2.providers.${providerService.providerID}.failure") ? baseURL + grailsApplication.config.getProperty("oauth2.providers.${providerService.providerID}.failure") : null
            log.debug("Failure URL: " + failureUrl)
            def apiKey = System.getenv("${providerService.providerID.toUpperCase()}_API_KEY") ?: grailsApplication.config.getProperty("oauth2.providers.${providerService.providerID}.key")
            def apiSecret = System.getenv("${providerService.providerID.toUpperCase()}_API_SECRET") ?: grailsApplication.config.getProperty("oauth2.providers.${providerService.providerID}.secret")
            log.debug("API Key: " + apiKey + ", Secret: " + apiSecret)
            def providerConfiguration = new OAuth2ProviderConfiguration(
                    apiKey: apiKey,
                    apiSecret: apiSecret,
                    callbackUrl: callbackURL,
                    successUrl: successUrl,
                    failureUrl: failureUrl,
                    debug: grailsApplication.config.getProperty('oauth2.debug')
            )
            providerService.init(providerConfiguration)
        }
    }

    /**
     * @return The base url
     */
    def String getBaseUrl() {
        grailsApplication.config.getProperty('grails.serverURL') ?: "http://localhost:${System.getProperty('server.port', '8080')}"
    }

    /**
     * @param providerName
     * @return The successurl for the provider service
     */
    def String getSuccessUrl(String providerName) {
        def providerService = getProviderService(providerName)
        providerService.successUrl ?: baseUrl + "/oauth2/" + providerName + "/success"
    }

    /**
     * @param providerName
     * @return The failureUrl for the provider service
     */
    def String getFailureUrl(String providerName) {
        def providerService = getProviderService(providerName)
        providerService.failureUrl ?: baseUrl + "/oauth2/" + providerName + "/success"
    }

    /**
     * @return The uri pointing to the page ask to link or create account
     */
    def getAskToLinkOrCreateAccountUri() {
        def askToLinkOrCreateAccountUri = grailsApplication.config.grails.plugin.springsecurity.oauth.registration.askToLinkOrCreateAccountUri ?: '/oauth2/askToLinkOrCreateAccount'
        return askToLinkOrCreateAccountUri
    }

    /**
     * Get OAuth2ProviderService
     * @param providerID
     * @return An OAuth2ProviderService implementation
     */
    def OAuth2ProviderService getProviderService(String providerID) {
        if (!providerServiceMap.get(providerID)) {
            log.error("There is no providerService for " + providerID)
            throw new OAuth2Exception("No provider '${providerID}'")
        }
        providerServiceMap.get(providerID)
    }

    /**
     * @param providerName
     * @return The session key
     */
    String sessionKeyForAccessToken(String providerName) {
        return "OAuth2:access-t:${providerName}"
    }

    /**
     * @return The OAuthID class name
     */
    protected String lookupOAuthIdClassName() {
        def domainClass = grailsApplication.config.grails.plugin.springsecurity.oauth.domainClass ?: 'OAuthID'
        return domainClass
    }

    /**
     * @return The OAuthID class
     */
    protected Class<?> lookupOAuthIdClass() {
        grailsApplication.getDomainClass(lookupOAuthIdClassName()).clazz
    }
}
