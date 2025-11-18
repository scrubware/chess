package exceptions;

public class PasswordIncorrectException extends RuntimeException {
    public PasswordIncorrectException() {
        super("Password incorrect!");
    }
}
