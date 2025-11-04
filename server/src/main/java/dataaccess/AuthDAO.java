package dataaccess;

import model.AuthData;

public interface AuthDAO {

    String getUsername(String authToken);
    AuthData createAuth(String username);
    boolean authExists(String authToken);
    void deleteAuth(String authToken);
}
