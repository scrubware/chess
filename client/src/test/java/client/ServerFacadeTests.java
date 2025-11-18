package client;

import exceptions.BadRequestException;
import exceptions.InvalidAuthTokenException;
import exceptions.InvalidGameIDException;
import model.AuthData;
import network.ServerFacade;
import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;
import java.net.URISyntaxException;


public class ServerFacadeTests {

    private static ServerFacade facade;
    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeEach
    public void clear() {
        server.clear();
    }


    @Test
    public void register() throws URISyntaxException, IOException, InterruptedException {
        AuthData auth = facade.register("username","password","email");
        Assertions.assertNotNull(auth);
        Assertions.assertEquals("username",auth.username());
        Assertions.assertNotNull(auth.authToken());
    }

    @Test
    public void registerNegative() {
        Assertions.assertThrows(BadRequestException.class,() -> facade.register(null,"password","email"));
    }

    @Test
    public void login() throws URISyntaxException, IOException, InterruptedException {
        Assertions.assertDoesNotThrow(() -> facade.register("username","password","email"));
        AuthData auth = facade.login("username","password");
        Assertions.assertNotNull(auth);
        Assertions.assertEquals("username",auth.username());
        Assertions.assertNotNull(auth.authToken());
    }

    @Test
    public void loginNegative() {
        Assertions.assertThrows(BadRequestException.class,() -> facade.register(null,"password","email"));
    }

    @Test
    public void logout() throws URISyntaxException, IOException, InterruptedException {
        var auth = facade.register("username","password","email");
        Assertions.assertDoesNotThrow(() -> facade.logout(auth));
    }

    @Test
    public void logoutNegative() {
        Assertions.assertThrows(InvalidAuthTokenException.class,() -> facade.logout(new AuthData("null","null")));
    }

    @Test
    public void createGame() throws URISyntaxException, IOException, InterruptedException {
        var auth = facade.register("username","password","email");
        Assertions.assertDoesNotThrow(() -> facade.createGame(auth,"game name!"));
    }

    @Test
    public void createGameNegative() throws URISyntaxException, IOException, InterruptedException {
        Assertions.assertThrows(InvalidAuthTokenException.class,
                () -> facade.createGame(new AuthData("null","null"),null));
    }

    @Test
    public void listGames() throws URISyntaxException, IOException, InterruptedException {
        var auth = facade.register("username","password","email");
        Assertions.assertDoesNotThrow(() -> facade.listGames(auth));
        Assertions.assertEquals(0, facade.listGames(auth).size());
        Assertions.assertDoesNotThrow(() -> facade.createGame(auth,"game name!"));
        Assertions.assertEquals(1, facade.listGames(auth).size());
    }

    @Test
    public void listGamesNegative() {
        Assertions.assertThrows(InvalidAuthTokenException.class,
                () -> facade.listGames(new AuthData("null","null")));
    }

    @Test
    public void joinGame() throws URISyntaxException, IOException, InterruptedException {
        var auth = facade.register("username","password","email");
        int id = facade.createGame(auth,"game name!");
        Assertions.assertDoesNotThrow(() -> facade.joinGame(auth,"WHITE",id));
    }

    @Test
    public void joinGameNegative() throws URISyntaxException, IOException, InterruptedException {
        var auth = facade.register("username","password","email");
        Assertions.assertThrows(InvalidGameIDException.class,() -> facade.joinGame(auth,"WHITE",0));
    }
}
