Spring Security OAuth2 Plugin
=======
[![Build Status](https://travis-ci.org/grails-plugins/grails-spring-security-oauth2.svg?branch=master)](https://travis-ci.org/grails-plugins/grails-spring-security-oauth2) [ ![Download](https://api.bintray.com/packages/grails/plugins/spring-security-oauth2/images/download.svg) ](https://bintray.com/grails/plugins/spring-security-oauth2/_latestVersion)

Main differences with the Grails 2 plugin:

- no more dependency on https://github.com/antony/grails-oauth-scribe but some code of that plugin was ported in this
- Relies on [Scribejava](https://github.com/scribejava/scribejava) to do most of the OAuth logic
- simplest code as possible
- easy to extend

Installation
------------
Use [v1.2.x](https://github.com/grails-plugins/grails-spring-security-oauth2-google/releases/tag/v1.2.0) for Grails 3.0 to 3.2

Use v1.3.x for Grails 3.3+.

Add the following dependencies in `build.gradle`
```
dependencies {
...
    compile 'org.grails.plugins:spring-security:3.2.+'
    compile 'org.grails.plugins:spring-security-oauth2:1.2.0'
...
}
```
You will also need at least one provider extension, i.e the `grails-spring-security-oauth2-google` plugin
Change the version to reflect the actual version you would like to use.

You can configure the following parameters in your `application.yml`. This is fully optional
```
grails:
    plugin:
        springsecurity:
            oauth2:
                active: true    #whether the whole plugin is active or not
                registration:
                    askToLinkOrCreateAccountUri: '/oauth2/ask' # The URI that is called to aks the user to either create a new account or link to an existing account
                    roleNames: ['ROLE_USER'] #A list of role names that should be automatically granted to an OAuth User. The roles will be created if they do not exist
```

Once you have an User domain class, initialize this plugin by using the init script `grails init-oauth2 <domain-class-package> <user-class-name> <oauthid-class-name>`
In example: `grails init-oauth2 com.yourapp User OAuthID`
That will create the domain class `com.yourapp.oAuthID`

Finally add:
```groovy
static hasMany = [oAuthIDs: OAuthID]
```
to your user domain class.

Extensions
----------

List of known extension
* [Google](https://github.com/grails-plugins/grails-spring-security-oauth2-google)
* [Facebook](https://github.com/MatrixCrawler/grails-spring-security-oauth2-facebook)
* [Github](https://github.com/rpalcolea/grails-spring-security-oauth2-github)
* [Outlook](https://github.com/andreperegrina/grails-spring-security-oauth2-outlook)
* [VK](https://github.com/purpleraven/grails-spring-security-oauth2-vk)
* [Odnoklassniki](https://github.com/purpleraven/grails-spring-security-oauth2-ok)


How to create a new provider plugin
-----------------------------------
1. Create a new plugin with `grails create-plugin spring-security-oauth2-myProvider`
2. Add the following plugins as dependency in `build.gradle`:
    * `provided 'org.grails.plugins:spring-security-core:3.+'`
    * `provided 'org.grails.plugins:spring-security-oauth2:1.1.+'`
3. Create a service in your plugin that extends `OAuth2AbstractProviderService` and implement the abstract methods. You can override the other methods for fine-tuning if needed.


License
-------

Apache 2
