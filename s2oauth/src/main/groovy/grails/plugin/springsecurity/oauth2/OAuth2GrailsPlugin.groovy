package grails.plugin.springsecurity.oauth2

import grails.plugin.springsecurity.oauth2.service.OAuth2ProviderService
import grails.plugins.Plugin

@SuppressWarnings('PackageName')
class OAuth2GrailsPlugin extends Plugin {

    // the version or versions of Grails the plugin is designed for
    String grailsVersion = "3.0.0 > *"
    // resources that are excluded from plugin packaging
    //def pluginExcludes = [ "grails-app/views/error.gsp"    ]
    def dependsOn = ["grails-spring-security-core": "*>3.0.0"]

    // TODO Fill in these fields
    def title = "Spring Security OAuth2 Plugin" // Headline display name of the plugin
    def author = "Your name"
    def authorEmail = ""
    def description = 'This plugin provides the capability to authenticate via oauth. Depends on grails-spring-security-core'
    def profiles = ['web']

    // URL to the plugin's documentation
//    def documentation = "http://grails.org/plugin/s2oauth"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Any additional developers beyond the author specified above.
    def developers = [[name: "Johannes Brunswicker", email: "matrixcrawler@matrixcrawler.net"]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    Closure doWithSpring() {
        { ->
            // TODO Implement runtime spring config (optional)
        }
    }

    void doWithDynamicMethods() {
        // TODO Implement registering dynamic methods to classes (optional)
    }

    void doWithApplicationContext() {
        //TODO
        def OAuth2BaseService oAuth2BaseService = grailsApplication.mainContext.getBean('OAuth2BaseService') as OAuth2BaseService
        for (serviceClass in grailsApplication.serviceClasses) {
            if (OAuth2ProviderService.class.isAssignableFrom(serviceClass.clazz)) {
                println "Found valid oauth provider service: ${serviceClass.clazz}"
                String beanName = "${serviceClass.logicalPropertyName}Service"
                OAuth2ProviderService providerService = grailsApplication.mainContext.getBean(beanName) as OAuth2ProviderService
                oAuth2BaseService.registerProvider(providerService)
            }
        }
    }

    void onChange(Map<String, Object> event) {
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    void onConfigChange(Map<String, Object> event) {
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    void onShutdown(Map<String, Object> event) {
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
