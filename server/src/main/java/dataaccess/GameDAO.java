package dataaccess;

import model.GameData;
import java.util.Collection;

public interface GameDAO {
    GameData getGame(GameData gameData);
    void updateGame(GameData gameData);
    GameData createGame(GameData gameData);
    Collection<GameData> listGames();
}
