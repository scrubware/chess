package exceptions;

public class UnknownException extends RuntimeException {
    public UnknownException() {
        super("unknown issue");
    }
}
