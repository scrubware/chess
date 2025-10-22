package service;

import java.util.Collection;

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


}
