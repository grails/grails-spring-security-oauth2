package grails.plugin.springsecurity.oauth2.exception;

/**
 * Always code as if the guy who ends up maintaining your code
 * will be a violent psychopath who knows where you live.
 * Code for readability.
 * <p>
 * John F. Woods
 * <p>
 * Created by Johannes on 06.04.2016.
 */
public class OAuth2Exception extends RuntimeException {
    public OAuth2Exception(String message) {
        super(message);
    }

    public OAuth2Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
