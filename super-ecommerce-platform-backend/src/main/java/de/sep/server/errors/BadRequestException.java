package de.sep.server.errors;

public class BadRequestException extends Exception{
    public BadRequestException(String message) {
        super(message);
    }
}
