package de.sep.server.errors;

public class InternalServerErrorException extends Exception {
    public InternalServerErrorException(String message) {
        super(message);
    }
}
