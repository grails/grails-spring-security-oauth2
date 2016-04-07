package grails.plugin.springsecurity.oauth2.facebook.token

import com.github.scribejava.core.model.OAuth2AccessToken
import grails.plugin.springsecurity.oauth2.token.OAuth2SpringToken

/**
 * Spring Security authentication token for Facebook users. It's a standard {@link OAuth2SpringToken}
 * that returns the Facebook name as the principal.
 *
 * @author <a href='mailto:cazacugmihai@gmail.com'>Mihai Cazacu</a>
 * @author <a href='mailto:enrico@comiti.name'>Enrico Comiti</a>
 * @author Thierry Nicola
 */
class FacebookOAuth2SpringToken extends OAuth2SpringToken {

    public static final String PROVIDER_NAME = 'facebook'

    String profileId

    FacebookOAuth2SpringToken(OAuth2AccessToken accessToken, String profileId) {
        super(accessToken)
        this.profileId = profileId
        this.principal = profileId
    }

    String getSocialId() {
        return profileId
    }

    String getScreenName() {
        return profileId
    }

    String getProviderName() {
        return PROVIDER_NAME
    }

}
