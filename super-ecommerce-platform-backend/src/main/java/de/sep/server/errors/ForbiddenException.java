package de.sep.server.errors;

public class ForbiddenException extends Exception {
    public ForbiddenException(String message) {
        super(message);
    }
}
