package dataaccess;

import model.AuthData;

public interface AuthDAO {

    AuthData createAuth(String username);
    boolean authExists(String authToken);
    void deleteAuth(String authToken);
}
