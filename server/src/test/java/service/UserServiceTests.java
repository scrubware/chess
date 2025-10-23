package service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;

import requests.LoginRequest;

import model.UserData;


public class UserServiceTests {

    @Test
    @DisplayName("Valid Register")
    public void register() {
        var authDAO = new MemoryAuthDAO();
        var userDAO = new MemoryUserDAO();

        var userService = new UserService(authDAO, userDAO);

        var authData = userService.register(new UserData("username","password","email"));

        Assertions.assertTrue(authDAO.authExists(authData.authToken()));
    }

    @Test
    @DisplayName("Invalid Register")
    public void registerBad() {
        var authDAO = new MemoryAuthDAO();
        var userDAO = new MemoryUserDAO();

        var userService = new UserService(authDAO, userDAO);

        userService.register(new UserData("u","p","e"));

        Assertions.assertThrows(AlreadyTakenException.class, () -> userService.register(new UserData("u","p","e")));
    }

    @Test
    @DisplayName("Valid Logout")
    public void logout() {
        var authDAO = new MemoryAuthDAO();
        var userDAO = new MemoryUserDAO();

        var userService = new UserService(authDAO, userDAO);

        var authData = userService.register(new UserData("username","password","email"));

        Assertions.assertTrue(authDAO.authExists(authData.authToken()));

        userService.logout(authData.authToken());

        Assertions.assertFalse(authDAO.authExists(authData.authToken()));
    }

    @Test
    @DisplayName("Invalid Logout")
    public void logoutBad() {
        var authDAO = new MemoryAuthDAO();
        var userDAO = new MemoryUserDAO();

        var userService = new UserService(authDAO, userDAO);

        var authData = userService.register(new UserData("username","password","email"));

        Assertions.assertTrue(authDAO.authExists(authData.authToken()));

        Assertions.assertThrows(InvalidAuthTokenException.class, () -> userService.logout(null));
    }

    @Test
    @DisplayName("Valid Login")
    public void login() {
        var authDAO = new MemoryAuthDAO();
        var userDAO = new MemoryUserDAO();

        var userService = new UserService(authDAO, userDAO);

        var authData = userService.register(new UserData("username","password","email"));

        Assertions.assertTrue(authDAO.authExists(authData.authToken()));

        userService.logout(authData.authToken());

        Assertions.assertFalse(authDAO.authExists(authData.authToken()));

        var authDataTwo = userService.login(new LoginRequest("username","password"));

        Assertions.assertTrue(authDAO.authExists(authDataTwo.authToken()));
    }

    @Test
    @DisplayName("Invalid Login")
    public void loginBad() {
        var authDAO = new MemoryAuthDAO();
        var userDAO = new MemoryUserDAO();

        var userService = new UserService(authDAO, userDAO);

        var authData = userService.register(new UserData("username","password","email"));

        Assertions.assertTrue(authDAO.authExists(authData.authToken()));

        userService.logout(authData.authToken());

        Assertions.assertFalse(authDAO.authExists(authData.authToken()));

        Assertions.assertThrows(UserNotRegisteredException.class, () -> userService.login(new LoginRequest("womp","password")));
    }
}
