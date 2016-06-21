Spring Security OAuth2 Plugin
=======

[![Build Status](https://travis-ci.org/MatrixCrawler/grails-spring-security-oauth2.svg?branch=master)](https://travis-ci.org/MatrixCrawler/grails-spring-security-oauth2)

Main differences with the Grails 2 plugin:

- no more dependency on https://github.com/antony/grails-oauth-scribe but some code of that plugin was ported in this
- Relies on [Scribejava](https://github.com/scribejava/scribejava) to do most of the OAuth logic
- simplest code as possible
- easy to extend

How to create a new provider plugin
-----------------------------------
1. Create a new plugin with `grails create-plugin spring-security-oauth2-myProvider`
2. Add the following plugins as dependency in `build.gradle`:
    * `provided 'org.grails.plugins:spring-security-core:3.+'`
    * `provided 'org.grails.plugins:spring-security-oauth2:0.7+'`
3. Create a service in your plugin that extends `OAuth2AbstractProviderService` and implement the abstract methods. You can override the other methods for fine-tuning if needed.


License
-------

Apache 2