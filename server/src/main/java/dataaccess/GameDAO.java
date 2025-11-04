package dataaccess;

import model.GameData;
import java.util.Collection;

public interface GameDAO {
    GameData getGame(int gameID) throws DataAccessException;
    boolean updateGame(int gameID, GameData gameData) throws DataAccessException;
    int createGame(String name) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
}
