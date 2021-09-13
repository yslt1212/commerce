package de.sep.server.util;

public enum ResponseMessages {
    SUCCESSFUL("Successfully fetched Data"),
    NOT_FOUND("Did not find what you are looking for"),
    SERVER_ERROR("Something went terribly wrong"),
    BAD_REQEUST("Leider war eine Ihrer Eingaben nicht valide. Bitte ändern Sie Ihre Eigaben");

    private String response;

    ResponseMessages(String s) {
        this.response = s;
    }

    public String getResponse() {
        return response;
    }
}
