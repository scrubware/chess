package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class DatabaseGameDAO implements GameDAO {
    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void updateGame(int gameID, GameData gameData) {

    }

    @Override
    public int createGame(String name) {
        return 0;
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }
}
