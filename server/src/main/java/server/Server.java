package server;

import io.javalin.*;

import handlers.AdminHandler;
import handlers.UserHandler;
import handlers.GameHandler;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        var adminHandler = new AdminHandler();
        javalin.delete("/db",adminHandler::handleClear);

        var userHandler = new UserHandler();
        javalin.post("/user",userHandler::handleRegister);
        javalin.post("/session",userHandler::handleLogin);
        javalin.delete("/session",userHandler::handleLogout);

        var gameHandler = new GameHandler();
        javalin.get("/game",gameHandler::handleListGames);
        javalin.post("/game",gameHandler::handleCreateGame);
        javalin.put("/game",gameHandler::handleJoinGame);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
