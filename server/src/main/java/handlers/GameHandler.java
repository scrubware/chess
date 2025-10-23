package handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.javalin.http.Context;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;

import results.ListGamesResult;

import service.BadRequestException;
import service.GameService;

import java.util.Objects;

public class GameHandler {

    private final Gson gson = new Gson();
    private final GameService gameService;

    public GameHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.gameService = new GameService(authDAO,gameDAO);
    }

    public void handleListGames(Context ctx) {
        String authToken = ctx.header("authorization");
        if (authToken == null) {
            throw new BadRequestException();
        }

        ListGamesResult games = gameService.listGames(authToken);

        System.out.println(gson.toJson(games));

        ctx.status(200);
        ctx.result(gson.toJson(games));
    }

    public void handleCreateGame(Context ctx) {
        String authToken = ctx.header("authorization");
        JsonElement name = gson.fromJson(ctx.body(), JsonObject.class).get("gameName");

        if (authToken == null || name == null) {
            throw new BadRequestException();
        }

        int game_id = gameService.createGame(authToken,name.getAsString());

        ctx.status(200);
        ctx.result("{\"gameID\": " + game_id + "}");
    }

    public void handleJoinGame(Context ctx) {
        String authToken = ctx.header("authorization");
        JsonElement gameID = gson.fromJson(ctx.body(), JsonObject.class).get("gameID");
        JsonElement playerColor = gson.fromJson(ctx.body(), JsonObject.class).get("playerColor");

        if (authToken == null || gameID == null || playerColor == null) {
            throw new BadRequestException();
        }

        String stringColor = playerColor.getAsString();

        gameService.joinGame(authToken, stringColor, gameID.getAsInt());
    }
}
