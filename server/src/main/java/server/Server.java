package server;

import io.javalin.*;

import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;

import handlers.AdminHandler;
import handlers.UserHandler;
import handlers.GameHandler;

import service.*;

import java.util.HashMap;
import java.util.Map;

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

        // Map status codes to Exception classes to avoid duplication
        HashMap<Class<? extends Exception>, Integer> exceptionCodes = new HashMap<>();
        exceptionCodes.put(BadRequestException.class,400);
        exceptionCodes.put(InvalidAuthTokenException.class,401);
        exceptionCodes.put(UserNotRegisteredException.class,401);
        exceptionCodes.put(PasswordIncorrectException.class,401);
        exceptionCodes.put(UsernameAlreadyTakenException.class,403);
        exceptionCodes.put(InvalidGameIDException.class,500);

        // Handle those exceptions and send out the corresponding status code & error message.
        exceptionCodes.forEach((k, v) -> {
            javalin.exception(k, (e, ctx) -> {
                ctx.status(v).json(Map.of("message", "Error: " + e.getMessage()));
            });
        });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
