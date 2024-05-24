package grails.plugin.springsecurity.oauth2

import com.github.scribejava.apis.GoogleApi20
import com.github.scribejava.core.builder.api.DefaultApi20
import com.github.scribejava.core.model.OAuth2AccessToken
import grails.plugin.springsecurity.oauth2.service.OAuth2AbstractProviderService
import grails.plugin.springsecurity.oauth2.token.OAuth2SpringToken
import grails.testing.gorm.DataTest
import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class SpringSecurityOauth2BaseServiceSpec extends Specification implements DataTest, ServiceUnitTest<SpringSecurityOauth2BaseService> {

    String providerName = "google"
    String propertyNamespace = "grails.plugin.springsecurity.oauth2.providers"
    OAuth2AbstractProviderService oauthProviderService

    def setup() {
        oauthProviderService = new OAuth2AbstractProviderService() {

            @Override
            String getProviderID() {
                providerName
            }

            @Override
            Class<? extends DefaultApi20> getApiClass() {
                return GoogleApi20
            }

            @Override
            String getProfileScope() {
                return null
            }

            @Override
            String getScopes() {
                return null
            }

            @Override
            String getScopeSeparator() {
                return null
            }

            @Override
            OAuth2SpringToken createSpringAuthToken(OAuth2AccessToken accessToken) {
                return null
            }
        }
    }

    void "relative URLs"() {
        given:
        grailsApplication.config."${propertyNamespace}.${providerName}.api_key" = "api_key"
        grailsApplication.config."${propertyNamespace}.${providerName}.api_secret" = "api_secret"
        grailsApplication.config."${propertyNamespace}.${providerName}.successUri" = "/oauth2/${providerName}/success"
        grailsApplication.config."${propertyNamespace}.${providerName}.failureUri" = "/oauth2/${providerName}/failure"
        grailsApplication.config."${propertyNamespace}.${providerName}.callback" = "/oauth2/${providerName}/callback"
        service.registerProvider(oauthProviderService)

        expect:
        service.getFailureUrl(providerName) == "http://localhost:8080/oauth2/${providerName}/failure"
        service.getSuccessUrl(providerName) == "http://localhost:8080/oauth2/${providerName}/success"
        service.getProviderService(providerName).providerConfiguration.getCallbackUrl() == "http://localhost:8080/oauth2/${providerName}/callback"
    }


    void "absolute URLs"() {
        given:
        String otherDomain = "http://other.domain"
        grailsApplication.config."${propertyNamespace}.${providerName}.api_key" = "api_key"
        grailsApplication.config."${propertyNamespace}.${providerName}.api_secret" = "api_secret"
        grailsApplication.config."${propertyNamespace}.${providerName}.successUri" = "${otherDomain}/oauth2/${providerName}/success"
        grailsApplication.config."${propertyNamespace}.${providerName}.failureUri" = "${otherDomain}/oauth2/${providerName}/failure"
        grailsApplication.config."${propertyNamespace}.${providerName}.callback" = "${otherDomain}/oauth2/${providerName}/callback"
        service.registerProvider(oauthProviderService)

        expect:
        service.getFailureUrl(providerName) == "${otherDomain}/oauth2/${providerName}/failure"
        service.getSuccessUrl(providerName) == "${otherDomain}/oauth2/${providerName}/success"
        service.getProviderService(providerName).providerConfiguration.getCallbackUrl() == "${otherDomain}/oauth2/${providerName}/callback"
    }
}
