package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashSet;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO {

    HashSet<UserData> users;

    @Override
    public UserData getUser(String username) {
        for (var user : users) {
            if (Objects.equals(user.username(), username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void createUser(UserData userData) {
        users.add(userData);
    }
}
