package dataaccess;

import model.GameData;
import java.util.Collection;

public interface GameDAO extends ClearableDAO {
    GameData getGame(int game_id);
    void updateGame(GameData gameData);
    int createGame(String name);
    Collection<GameData> listGames();
}
