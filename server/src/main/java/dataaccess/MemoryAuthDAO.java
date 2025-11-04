package dataaccess;

import model.AuthData;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {

    HashSet<AuthData> auths = new HashSet<>();

    @Override
    public String getUsername(String authToken) {
        for (var auth : auths) {
            if (Objects.equals(auth.authToken(), authToken)) {
                return auth.username();
            }
        }
        return null;
    }

    @Override
    public AuthData createAuth(String username) {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken,username);
        auths.add(authData);
        return authData;
    }

    @Override
    public boolean authExists(String authToken) {
        for (var auth : auths) {
            if (Objects.equals(auth.authToken(), authToken)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteAuth(String authToken) {
        auths.removeIf(auth -> Objects.equals(auth.authToken(), authToken));
    }

    public void clear() {
        auths.clear();
    }
}
