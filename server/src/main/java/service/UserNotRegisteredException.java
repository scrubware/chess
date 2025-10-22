package service;

public class UserNotRegisteredException extends RuntimeException {
    public UserNotRegisteredException() {
        super("Error: unauthorized");
    }
}
