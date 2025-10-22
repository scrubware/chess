package handlers;

import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.http.Context;

public class GameHandler {

    private UserDAO userDAO;
    private GameDAO gameDAO;

    public GameHandler(UserDAO userDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    public void handleListGames(Context ctx) {

    }

    public void handleCreateGame(Context ctx) {

    }

    public void handleJoinGame(Context ctx) {

    }
}
