package service;

public class PasswordIncorrectException extends RuntimeException {
    public PasswordIncorrectException() {
        super("Error: unauthorized");
    }
}
