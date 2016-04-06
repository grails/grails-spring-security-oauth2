package grails.plugins.s2oauth

import grails.plugin.springsecurity.oauth2.OAuth2SpringToken
import org.scribe.model.Token
import org.scribe.model.Verifier
import org.scribe.oauth.OAuthService

public interface S2oauthProviderService {
    void init(S2oauthProviderConfiguration providerConfig);
    String providerId();
    OauthVersion supportedOauthVersion();
    OAuthService oauthClient();
    String getAuthorizationUrl(Token requestToken);
    Token getAccessToken(Token requestToken, Verifier verifier);
    OAuth2SpringToken createAuthToken(Token accessToken);
}
