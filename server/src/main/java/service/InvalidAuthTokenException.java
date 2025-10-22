package service;

public class InvalidAuthTokenException extends RuntimeException {
    public InvalidAuthTokenException() {
        super("unauthorized");
    }
}
