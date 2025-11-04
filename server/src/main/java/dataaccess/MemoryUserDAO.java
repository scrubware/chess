package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashSet;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO {

    HashSet<UserData> users = new HashSet<>();

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
    public boolean createUser(UserData userData) {
        users.add(userData);
        return true;
    }

    public void clear() {
        users.clear();
    }
}
