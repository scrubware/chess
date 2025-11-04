package service;

import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import model.UserData;

public class GameServiceTests {

    @BeforeEach
    public void clearBefore() {
        var clearDAO = new DatabaseClearDAO();
        Assertions.assertDoesNotThrow(clearDAO::clear);
    }

    @Test
    @DisplayName("Create Game Valid")
    public void createGame() throws DataAccessException {
        var authDAO = new DatabaseAuthDAO();
        var gameDAO = new DatabaseGameDAO();
        var userDAO = new DatabaseUserDAO();

        var userService = new UserService(authDAO, userDAO);
        var gameService = new GameService(authDAO, gameDAO);

        var authData = userService.register(new UserData("username","password","email"));

        Assertions.assertEquals(1,gameService.createGame(authData.authToken(),"game"));
    }

    @Test
    @DisplayName("Create Game Invalid")
    public void createGameBad() {
        var authDAO = new DatabaseAuthDAO();
        var gameDAO = new DatabaseGameDAO();

        var gameService = new GameService(authDAO, gameDAO);

        Assertions.assertThrows(InvalidAuthTokenException.class, () -> gameService.createGame(null,"yay"));
    }

    @Test
    @DisplayName("Join Game Valid")
    public void joinGame() throws DataAccessException {

        var authDAO = new DatabaseAuthDAO();
        var gameDAO = new DatabaseGameDAO();
        var userDAO = new DatabaseUserDAO();

        var userService = new UserService(authDAO, userDAO);
        var gameService = new GameService(authDAO, gameDAO);

        var authData = userService.register(new UserData("username","password","email"));

        var gameID = gameService.createGame(authData.authToken(),"game");

        Assertions.assertDoesNotThrow(() -> gameService.joinGame(authData.authToken(),"WHITE",gameID));
    }

    @Test
    @DisplayName("Join Game Invalid")
    public void joinGameBad() throws DataAccessException {

        var authDAO = new DatabaseAuthDAO();
        var gameDAO = new DatabaseGameDAO();
        var userDAO = new DatabaseUserDAO();

        var userService = new UserService(authDAO, userDAO);
        var gameService = new GameService(authDAO, gameDAO);

        var authData = userService.register(new UserData("username","password","email"));
        var gameID = gameService.createGame(authData.authToken(),"game");
        Assertions.assertThrows(BadRequestException.class,() -> gameService.joinGame(authData.authToken(),"GREEBLE-DORP",gameID));
    }

    @Test
    @DisplayName("List Games")
    public void listGames() throws DataAccessException {

        var authDAO = new DatabaseAuthDAO();
        var gameDAO = new DatabaseGameDAO();
        var userDAO = new DatabaseUserDAO();

        var gameService = new GameService(authDAO,gameDAO);
        var userService = new UserService(authDAO,userDAO);

        var userData = new UserData("username", "password", "mail@mail.com");
        var authData = userService.register(userData);

        gameService.createGame(authData.authToken(), "game");

        Assertions.assertEquals(1, gameService.listGames(authData.authToken()).games().size());

        gameService.createGame(authData.authToken(), "game2");

        Assertions.assertEquals(2, gameService.listGames(authData.authToken()).games().size());
    }

    @Test
    @DisplayName("List Games")
    public void listGamesBad() throws DataAccessException {

        var authDAO = new DatabaseAuthDAO();
        var gameDAO = new DatabaseGameDAO();
        var userDAO = new DatabaseUserDAO();

        var gameService = new GameService(authDAO,gameDAO);
        var userService = new UserService(authDAO,userDAO);

        var userData = new UserData("username","password","mail@mail.com");
        var authData = userService.register(userData);
        gameService.createGame(authData.authToken(),"game");
        Assertions.assertThrows(InvalidAuthTokenException.class, () -> gameService.listGames(null));
    }


}
