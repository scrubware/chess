package dataaccess;

import model.AuthData;
import model.UserData;

public interface UserDAO {

    UserData getUser(String username);
    boolean createUser(UserData userData);
}
