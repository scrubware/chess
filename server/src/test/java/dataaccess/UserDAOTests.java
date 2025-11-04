package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserDAOTests {

    @BeforeEach
    public void clear() {
        var clearDAO = new DatabaseClearDAO();
        clearDAO.clear();
    }

    @Test
    @DisplayName("Create User")
    public void createUser() {
        var userDAO = new DatabaseUserDAO();
        var userData = new UserData("username","password","email@email.com");

        Assertions.assertTrue(userDAO.createUser(userData));
    }

    @Test
    @DisplayName("Create User Negative")
    public void createUserNegative() {
        var userDAO = new DatabaseUserDAO();
        var userData = new UserData("username","password","email@email.com");

        userDAO.createUser(userData);
        Assertions.assertFalse(userDAO.createUser(userData));
    }

    @Test
    @DisplayName("Get User")
    public void getUser() {
        var userDAO = new DatabaseUserDAO();
        var userData = new UserData("username","password","email@email.com");

        userDAO.createUser(userData);
        Assertions.assertEquals(userData.username(),userDAO.getUser("username").username());
    }

    @Test
    @DisplayName("Get User Negative")
    public void getUserNegative() {
        var userDAO = new DatabaseUserDAO();
        Assertions.assertNull(userDAO.getUser("username"));
    }
}
