package grails.plugins.s2oauth.dummy

import org.scribe.model.Token

import grails.plugin.springsecurity.oauth2.OAuth2SpringToken

class DummyOAuth2SpringToken extends OAuth2SpringToken {

   public static final String PROVIDER_NAME = 'dummy'

   String profileId

    DummyOAuth2SpringToken(Token accessToken, String profileId) {
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
