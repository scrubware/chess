package dataaccess;

import model.AuthData;

public interface AuthDAO {

    String getUsername(String authToken) throws DataAccessException ;
    AuthData createAuth(String username) throws DataAccessException ;
    boolean authExists(String authToken) throws DataAccessException ;
    void deleteAuth(String authToken) throws DataAccessException ;
}
