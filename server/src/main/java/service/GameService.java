package service;

import java.util.Objects;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;

import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.InvalidAuthTokenException;
import model.GameData;
import results.ListGamesResult;

public class GameService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException {
        if (!authDAO.authExists(authToken)) {
            throw new InvalidAuthTokenException();
        }
        return new ListGamesResult(gameDAO.listGames());
    }

    public int createGame(String authToken, String name) throws DataAccessException {
        if (!authDAO.authExists(authToken)) {
            throw new InvalidAuthTokenException();
        }
        return gameDAO.createGame(name);
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws DataAccessException {
        if (!authDAO.authExists(authToken)) {
            throw new InvalidAuthTokenException();
        }

        if (!(Objects.equals(playerColor, "WHITE") || Objects.equals(playerColor, "BLACK"))) {
            throw new BadRequestException();
        }

        GameData game;
        try {
            game = gameDAO.getGame(gameID);
        } catch (dataaccess.DataAccessException e) {
            throw new RuntimeException(e);
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
