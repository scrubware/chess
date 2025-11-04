package dataaccess;

import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AuthDAOTests {

    @BeforeEach
    public void clear() {
        var clearDAO = new DatabaseClearDAO();
        clearDAO.clear();
    }

    @Test
    @DisplayName("Create Auth")
    public void createAuth() {

        var authDAO = new DatabaseAuthDAO();
        Assertions.assertNotNull(authDAO.createAuth("username"));
    }

    @Test
    @DisplayName("Create Auth Negative")
    public void createAuthNegative() {

    }

    @Test
    @DisplayName("Get Username")
    public void getUsername() {

    }

    @Test
    @DisplayName("Get Username Negative")
    public void getUsernameNegative() {

    }

    @Test
    @DisplayName("Auth Exists")
    public void authExists() {

    }

    @Test
    @DisplayName("Auth Exists Negative")
    public void authExistsNegative() {

    }

    @Test
    @DisplayName("Delete Auth")
    public void deleteAuth() {

    }

    @Test
    @DisplayName("Delete Auth Negative")
    public void deleteAuthNegative() {

    }
}
