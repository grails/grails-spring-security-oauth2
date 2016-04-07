package grails.plugin.springsecurity.oauth2.dummy.token

import com.github.scribejava.core.model.OAuth2AccessToken

import grails.plugin.springsecurity.oauth2.token.OAuth2SpringToken

class DummyOAuth2SpringToken extends OAuth2SpringToken {

   public static final String PROVIDER_NAME = 'dummy'

   String profileId

    DummyOAuth2SpringToken(OAuth2AccessToken accessToken, String profileId) {
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
