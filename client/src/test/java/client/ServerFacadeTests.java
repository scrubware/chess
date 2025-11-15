package client;

import model.AuthData;
import network.ServerFacade;
import org.junit.jupiter.api.*;
import server.Server;


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
    public void register() {
        AuthData auth = facade.register("username","password","email");
        Assertions.assertNotNull();
    }

}
