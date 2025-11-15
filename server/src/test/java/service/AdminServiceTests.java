package service;





import dataaccess.*;
import exceptions.InvalidAuthTokenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.UserData;

public class AdminServiceTests {

    @BeforeEach
    public void clearBefore() {
        var clearDAO = new DatabaseClearDAO();
        Assertions.assertDoesNotThrow(clearDAO::clear);
    }

    @Test
    @DisplayName("Clear Database")
    public void clear() throws DataAccessException {

        var authDAO = new DatabaseAuthDAO();
        var gameDAO = new DatabaseGameDAO();
        var userDAO = new DatabaseUserDAO();
        var clearDAO = new DatabaseClearDAO();

        var gameService = new GameService(authDAO,gameDAO);
        var userService = new UserService(authDAO,userDAO);
        var adminService = new AdminService(clearDAO);

        var userData = new UserData("username","password","mail@mail.com");
        var authData = userService.register(userData);

        gameService.createGame(authData.authToken(),"game");

        Assertions.assertEquals(1,gameService.listGames(authData.authToken()).games().size());

        Assertions.assertDoesNotThrow(adminService::clear);

        // We deleted the auth.
        Assertions.assertThrows(InvalidAuthTokenException.class,() -> gameService.listGames(authData.authToken()));
    }
}
