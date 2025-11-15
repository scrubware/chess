package exceptions;

public class InvalidAuthTokenException extends RuntimeException {
    public InvalidAuthTokenException() {
        super("unauthorized");
    }
}
