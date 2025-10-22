package dataaccess;

import model.AuthData;

import java.util.HashSet;
import java.util.Objects;

public class MemoryAuthDAO implements AuthDAO {

    HashSet<AuthData> auths;

    @Override
    public AuthData createAuth(String username) {
        auths.add();
    }

    @Override
    public boolean authExists(AuthData authData) {
        for (var auth : auths) {
            if (Objects.equals(auth.authToken(), authData.authToken())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteAuth(AuthData authData) {
        auths.removeIf(auth -> Objects.equals(auth.authToken(), authData.authToken()));
    }
}
