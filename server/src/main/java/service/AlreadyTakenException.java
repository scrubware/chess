package service;

public class AlreadyTakenException extends RuntimeException {
    public AlreadyTakenException() {
        super("already taken");
    }
}
