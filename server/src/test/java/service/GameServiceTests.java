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

        try {
            clearDAO.clear();
        } catch(Exception _) {}
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
    public void createGameBad() throws DataAccessException {
        var authDAO = new DatabaseAuthDAO();
        var gameDAO = new DatabaseGameDAO();

        var gameService = new GameService(authDAO, gameDAO);

        Assertions.assertThrows(InvalidAuthTokenException.class, () -> gameService.createGame(null,"yay"));
    }

    @Test
    @DisplayName("Join Game Valid")
    public void joinGame() {

        var authDAO = new DatabaseAuthDAO();
        var gameDAO = new DatabaseGameDAO();
        var userDAO = new DatabaseUserDAO();

        var userService = new UserService(authDAO, userDAO);
        var gameService = new GameService(authDAO, gameDAO);

        try {
            var authData = userService.register(new UserData("username","password","email"));

            var gameID = gameService.createGame(authData.authToken(),"game");

            Assertions.assertDoesNotThrow(() -> gameService.joinGame(authData.authToken(),"WHITE",gameID));
        } catch (Exception _) {
            Assertions.fail();
        }

    }

    @Test
    @DisplayName("Join Game Invalid")
    public void joinGameBad() {

        var authDAO = new DatabaseAuthDAO();
        var gameDAO = new DatabaseGameDAO();
        var userDAO = new DatabaseUserDAO();

        var userService = new UserService(authDAO, userDAO);
        var gameService = new GameService(authDAO, gameDAO);

        try {
            var authData = userService.register(new UserData("username","password","email"));
            var gameID = gameService.createGame(authData.authToken(),"game");
            Assertions.assertThrows(BadRequestException.class,() -> gameService.joinGame(authData.authToken(),"GREEBLE-DORP",gameID));
        } catch (Exception _) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("List Games")
    public void listGames() {

        var authDAO = new DatabaseAuthDAO();
        var gameDAO = new DatabaseGameDAO();
        var userDAO = new DatabaseUserDAO();

        var gameService = new GameService(authDAO,gameDAO);
        var userService = new UserService(authDAO,userDAO);

        try {
            var userData = new UserData("username", "password", "mail@mail.com");
            var authData = userService.register(userData);

            gameService.createGame(authData.authToken(), "game");

            Assertions.assertEquals(1, gameService.listGames(authData.authToken()).games().size());

            gameService.createGame(authData.authToken(), "game2");

            Assertions.assertEquals(2, gameService.listGames(authData.authToken()).games().size());
        } catch (Exception _) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("List Games")
    public void listGamesBad() {

        var authDAO = new DatabaseAuthDAO();
        var gameDAO = new DatabaseGameDAO();
        var userDAO = new DatabaseUserDAO();

        var gameService = new GameService(authDAO,gameDAO);
        var userService = new UserService(authDAO,userDAO);

        try {
            var userData = new UserData("username","password","mail@mail.com");
            var authData = userService.register(userData);
            gameService.createGame(authData.authToken(),"game");
            Assertions.assertThrows(InvalidAuthTokenException.class, () -> gameService.listGames(null));
        } catch (Exception _) {
            Assertions.fail();
        }
    }


}
