package service;

public class InvalidGameIDException extends RuntimeException {
    public InvalidGameIDException() {
        super("no game with ID exists");
    }
}
