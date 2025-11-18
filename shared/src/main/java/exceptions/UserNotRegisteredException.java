package exceptions;

public class UserNotRegisteredException extends RuntimeException {
    public UserNotRegisteredException() {
        super("User not registered!");
    }
}
