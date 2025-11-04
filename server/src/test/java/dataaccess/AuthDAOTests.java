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
        Assertions.assertDoesNotThrow(clearDAO::clear);
    }

    @Test
    @DisplayName("Create Auth")
    public void createAuth() throws DataAccessException {
        var authDAO = new DatabaseAuthDAO();
        Assertions.assertNotNull(authDAO.createAuth("username"));
    }

    @Test
    @DisplayName("Create Auth Negative")
    public void createAuthNegative() throws DataAccessException {
        var authDAO = new DatabaseAuthDAO();
        Assertions.assertNull(authDAO.createAuth(null));
    }

    @Test
    @DisplayName("Get Username")
    public void getUsername() throws DataAccessException {
        var authDAO = new DatabaseAuthDAO();
        var authData = authDAO.createAuth("username");
        Assertions.assertEquals("username", authDAO.getUsername(authData.authToken()));
    }

    @Test
    @DisplayName("Get Username Negative")
    public void getUsernameNegative() throws DataAccessException {
        var authDAO = new DatabaseAuthDAO();
        Assertions.assertNull(authDAO.getUsername("random token"));
    }

    @Test
    @DisplayName("Auth Exists")
    public void authExists() throws DataAccessException {
        var authDAO = new DatabaseAuthDAO();
        var authData = authDAO.createAuth("username");
        Assertions.assertTrue(authDAO.authExists(authData.authToken()));
    }

    @Test
    @DisplayName("Auth Exists Negative")
    public void authExistsNegative() throws DataAccessException {
        var authDAO = new DatabaseAuthDAO();
        Assertions.assertFalse(authDAO.authExists("random token"));
    }

    @Test
    @DisplayName("Delete Auth")
    public void deleteAuth() throws DataAccessException {
        var authDAO = new DatabaseAuthDAO();
        var authData = authDAO.createAuth("username");
        authDAO.deleteAuth(authData.authToken());
        Assertions.assertFalse(authDAO.authExists(authData.authToken()));
    }

    @Test
    @DisplayName("Delete Auth Negative")
    public void deleteAuthNegative() throws DataAccessException {
        var authDAO = new DatabaseAuthDAO();
        var authData = authDAO.createAuth("username");
        authDAO.deleteAuth("random token");
        Assertions.assertTrue(authDAO.authExists(authData.authToken()));
    }
}
