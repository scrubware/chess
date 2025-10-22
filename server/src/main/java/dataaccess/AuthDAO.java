package dataaccess;

import model.AuthData;

public interface AuthDAO extends ClearableDAO {

    String getUsername(String authToken);
    AuthData createAuth(String username);
    boolean authExists(String authToken);
    void deleteAuth(String authToken);
}
