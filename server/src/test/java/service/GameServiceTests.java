package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.MemoryAuthDAO;

import model.UserData;

public class GameServiceTests {

    @Test
    @DisplayName("Create Game Valid")
    public void createGame() {
        var authDAO = new MemoryAuthDAO();
        var userDAO = new MemoryUserDAO();
        var gameDAO = new MemoryGameDAO();

        var userService = new UserService(authDAO, userDAO);
        var gameService = new GameService(authDAO, gameDAO);

        var authData = userService.register(new UserData("username","password","email"));

        Assertions.assertEquals(1,gameService.createGame(authData.authToken(),"game"));
    }

    @Test
    @DisplayName("Create Game Invalid")
    public void createGameBad() {
        var authDAO = new MemoryAuthDAO();
        var userDAO = new MemoryUserDAO();
        var gameDAO = new MemoryGameDAO();

        var userService = new UserService(authDAO, userDAO);
        var gameService = new GameService(authDAO, gameDAO);

        var authData = userService.register(new UserData("username","password","email"));

        Assertions.assertThrows(InvalidAuthTokenException.class, () -> gameService.createGame(null,"yay"));
    }

    @Test
    @DisplayName("Join Game Valid")
    public void joinGame() {

        var authDAO = new MemoryAuthDAO();
        var userDAO = new MemoryUserDAO();
        var gameDAO = new MemoryGameDAO();

        var userService = new UserService(authDAO, userDAO);
        var gameService = new GameService(authDAO, gameDAO);

        var authData = userService.register(new UserData("username","password","email"));

        var gameID = gameService.createGame(authData.authToken(),"game");

        Assertions.assertDoesNotThrow(() -> gameService.joinGame(authData.authToken(),"WHITE",gameID));
    }

    @Test
    @DisplayName("Join Game Invalid")
    public void joinGameBad() {

        var authDAO = new MemoryAuthDAO();
        var userDAO = new MemoryUserDAO();
        var gameDAO = new MemoryGameDAO();

        var userService = new UserService(authDAO, userDAO);
        var gameService = new GameService(authDAO, gameDAO);

        var authData = userService.register(new UserData("username","password","email"));

        var gameID = gameService.createGame(authData.authToken(),"game");

        Assertions.assertThrows(BadRequestException.class,() -> gameService.joinGame(authData.authToken(),"GREEBLE-DORP",gameID));
    }

    @Test
    @DisplayName("List Games")
    public void listGames() {

        var authDAO = new MemoryAuthDAO();
        var gameDAO = new MemoryGameDAO();
        var userDAO = new MemoryUserDAO();

        var gameService = new GameService(authDAO,gameDAO);
        var userService = new UserService(authDAO,userDAO);
        var adminService = new AdminService(authDAO,gameDAO,userDAO);

        var userData = new UserData("username","password","mail@mail.com");
        var authData = userService.register(userData);

        gameService.createGame(authData.authToken(),"game");

        Assertions.assertEquals(1,gameService.listGames(authData.authToken()).games().size());

        gameService.createGame(authData.authToken(),"game2");

        Assertions.assertEquals(2,gameService.listGames(authData.authToken()).games().size());
    }

    @Test
    @DisplayName("List Games")
    public void listGamesBad() {

        var authDAO = new MemoryAuthDAO();
        var gameDAO = new MemoryGameDAO();
        var userDAO = new MemoryUserDAO();

        var gameService = new GameService(authDAO,gameDAO);
        var userService = new UserService(authDAO,userDAO);
        var adminService = new AdminService(authDAO,gameDAO,userDAO);

        var userData = new UserData("username","password","mail@mail.com");
        var authData = userService.register(userData);

        gameService.createGame(authData.authToken(),"game");

        Assertions.assertThrows(InvalidAuthTokenException.class, () -> gameService.listGames(null));
    }


}
