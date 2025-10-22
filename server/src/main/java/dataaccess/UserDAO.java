package dataaccess;

import model.AuthData;
import model.UserData;

public interface UserDAO extends ClearableDAO {

    UserData getUser(String username);
    void createUser(UserData userData);
}
