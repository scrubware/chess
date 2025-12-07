package exceptions;

public class LockedGameException extends RuntimeException {
    public LockedGameException() {
        super("game is completed");
    }
}
