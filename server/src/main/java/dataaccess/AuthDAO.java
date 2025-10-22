package dataaccess;

import model.AuthData;

public interface AuthDAO {

    void createAuth(AuthData authData);
    boolean authExists(AuthData authData);
    void deleteAuth(AuthData authData);
}
