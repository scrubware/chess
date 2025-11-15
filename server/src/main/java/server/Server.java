package server;

import dataaccess.*;
import exceptions.*;
import io.javalin.*;

import handlers.AdminHandler;
import handlers.UserHandler;
import handlers.GameHandler;

import java.util.HashMap;

public class Server {

    private final Javalin javalin;

    private final AuthDAO authDAO = new DatabaseAuthDAO();
    private final GameDAO gameDAO = new DatabaseGameDAO();
    private final UserDAO userDAO = new DatabaseUserDAO();
    private final ClearDAO clearDAO = new DatabaseClearDAO();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        var adminHandler = new AdminHandler(clearDAO);
        javalin.delete("/db",adminHandler::handleClear);

        var userHandler = new UserHandler(authDAO, userDAO);
        javalin.post("/user",userHandler::handleRegister);
        javalin.post("/session",userHandler::handleLogin);
        javalin.delete("/session",userHandler::handleLogout);

        var gameHandler = new GameHandler(authDAO,gameDAO);
        javalin.get("/game",gameHandler::handleListGames);
        javalin.post("/game",gameHandler::handleCreateGame);
        javalin.put("/game",gameHandler::handleJoinGame);

        // Map status codes to Exception classes to avoid code duplication
        HashMap<Class<? extends Exception>, Integer> exceptionCodes = new HashMap<>();
        exceptionCodes.put(BadRequestException.class,400);
        exceptionCodes.put(InvalidAuthTokenException.class,401);
        exceptionCodes.put(UserNotRegisteredException.class,401);
        exceptionCodes.put(PasswordIncorrectException.class,401);
        exceptionCodes.put(AlreadyTakenException.class,403);
        exceptionCodes.put(InvalidGameIDException.class,500);

        // Handle those exceptions and send out the corresponding status code & error message
        exceptionCodes.forEach((k, v) -> {
            javalin.exception(k, (e, ctx) -> {
                ctx.status(v).result("{\"message\":\"Error: " + e.getMessage() + "\"}");
            });
        });

        // Handle any unhandled exceptions and return 500
        javalin.exception(Exception.class, (e, ctx) -> {
            ctx.status(500).result("{\"message\":\"Error: internal server issue\"}");
        });
    }

    public void clear() {
        try {
            clearDAO.clear();
        } catch (Exception e) {}
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
