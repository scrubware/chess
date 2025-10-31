package dataaccess;

import model.AuthData;

public class DatabaseAuthDAO implements AuthDAO {
    @Override
    public String getUsername(String authToken) {
        return "";
    }

    @Override
    public AuthData createAuth(String username) {
        return null;
    }

    @Override
    public boolean authExists(String authToken) {
        return false;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void clear() {

    }
}
