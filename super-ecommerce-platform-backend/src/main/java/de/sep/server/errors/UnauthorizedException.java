package de.sep.server.errors;

public class UnauthorizedException extends Exception {
    public UnauthorizedException(String cause) {
        super("You are not authorized to do that\nCause:"+cause);
    }

    public UnauthorizedException() {
        super("You are not authorized to do that");
    }
}
