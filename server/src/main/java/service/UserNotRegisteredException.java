package service;

public class UserNotRegisteredException extends RuntimeException {
    public UserNotRegisteredException() {
        super("unauthorized");
    }
}
