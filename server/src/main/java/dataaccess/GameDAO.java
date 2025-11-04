package dataaccess;

import model.GameData;
import java.util.Collection;

public interface GameDAO {
    GameData getGame(int gameID);
    void updateGame(int gameID, GameData gameData);
    int createGame(String name);
    Collection<GameData> listGames();
}
