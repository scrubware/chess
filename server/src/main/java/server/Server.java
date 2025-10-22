package server;

import io.javalin.*;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;

import handlers.AdminHandler;
import handlers.UserHandler;
import handlers.GameHandler;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        var authDAO = new MemoryAuthDAO();
        var gameDAO = new MemoryGameDAO();
        var userDAO = new MemoryUserDAO();

        var adminHandler = new AdminHandler(authDAO,gameDAO,userDAO);
        javalin.delete("/db",adminHandler::handleClear);

        var userHandler = new UserHandler(authDAO, userDAO);
        javalin.post("/user",userHandler::handleRegister);
        javalin.post("/session",userHandler::handleLogin);
        javalin.delete("/session",userHandler::handleLogout);

        var gameHandler = new GameHandler(authDAO,gameDAO);
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
