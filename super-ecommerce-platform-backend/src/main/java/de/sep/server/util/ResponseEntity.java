package de.sep.server.util;

// We use this class to generalise the Responses we give to the client request
// You can init it with the type of the Data you wanna apply
public class ResponseEntity<T> {

    String message;
    T data;
    int status;

    public ResponseEntity() {
        message = "";
        status = 200;
    }

    public ResponseEntity(String message, T data, int code) {
        this.message = message;
        this.data = data;
        this.status = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int code) {
        this.status = code;
    }
}
