package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;

import service.BadRequestException;
import service.GameService;

import model.GameData;

import java.util.Collection;

public class GameHandler {

    private final Gson gson = new Gson();
    private final GameService gameService;

    public GameHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.gameService = new GameService(authDAO,gameDAO);
    }

    public void handleListGames(Context ctx) {
        String authToken = ctx.header("authorization");
        if (authToken == null) throw new BadRequestException();

        Collection<GameData> games = gameService.listGames(authToken);
    }

    public void handleCreateGame(Context ctx) {
        String authToken = ctx.header("authorization");
        String name = gson.fromJson(ctx.body(), String.class);

        if (authToken == null || name == null) throw new BadRequestException();

        int game_id = gameService.createGame(authToken,name);

        ctx.status(200);
        ctx.result("{\"gameID\": " + game_id + "}");
    }

    public void handleJoinGame(Context ctx) {
        String authToken = ctx.header("authorization");
        if (authToken == null) throw new BadRequestException();
    }
}
