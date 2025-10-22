package handlers;

import io.javalin.http.Context;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;

import service.GameService;

public class GameHandler {

    private final GameService gameService;

    public GameHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.gameService = new GameService(authDAO,gameDAO);
    }

    public void handleListGames(Context ctx) {

    }

    public void handleCreateGame(Context ctx) {

    }

    public void handleJoinGame(Context ctx) {

    }
}
