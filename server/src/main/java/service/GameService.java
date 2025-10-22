package service;

import java.util.Collection;
import java.util.Objects;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;

import model.GameData;

public class GameService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public Collection<GameData> listGames(String authToken) {
        if (!authDAO.authExists(authToken)) throw new InvalidAuthTokenException();
        return gameDAO.listGames();
    }

    public int createGame(String authToken, String name) {
        if (!authDAO.authExists(authToken)) throw new InvalidAuthTokenException();
        return gameDAO.createGame(name);
    }

    public void joinGame(String authToken, String playerColor, int gameID) {
        if (!authDAO.authExists(authToken)) throw new InvalidAuthTokenException();

        GameData game = gameDAO.getGame(gameID);

        if (!(Objects.equals(playerColor, "WHITE") || Objects.equals(playerColor, "BLACK"))) {
            throw new BadRequestException();
        }

        boolean white = Objects.equals(playerColor, "WHITE");
        String username = authDAO.getUsername(authToken);

        if (white) {
            if (game.whiteUsername() == null) {
                gameDAO.updateGame(gameID, new GameData(gameID,username,game.blackUsername(),game.gameName(),game.game()));
            } else {
                throw new AlreadyTakenException();
            }
        }
        if (!white) {
            if (game.blackUsername() == null) {
                gameDAO.updateGame(gameID, new GameData(gameID,game.whiteUsername(),username,game.gameName(),game.game()));
            } else {
                throw new AlreadyTakenException();
            }
        }
    }
}
