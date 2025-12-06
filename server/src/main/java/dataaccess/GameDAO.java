package dataaccess;

import model.GameData;
import java.util.Collection;

public interface GameDAO {
    GameData getGame(int gameID) throws DataAccessException;
    boolean updateGame(int gameID, GameData gameData) throws DataAccessException;
    int createGame(String name) throws DataAccessException;
    void removeGame(int gameID);
    void lockGame(int gameID);
    boolean getGameLocked(int gameID);
    Collection<GameData> listGames() throws DataAccessException;
}
