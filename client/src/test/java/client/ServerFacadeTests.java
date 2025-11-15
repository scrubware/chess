package client;

import exceptions.BadRequestException;
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
    public void login() {
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

}
