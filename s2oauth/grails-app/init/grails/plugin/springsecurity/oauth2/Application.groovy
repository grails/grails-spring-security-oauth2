package grails.plugin.springsecurity.oauth2

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

@SuppressWarnings('PackageName')
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}
