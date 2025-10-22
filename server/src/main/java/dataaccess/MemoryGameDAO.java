package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class MemoryGameDAO implements GameDAO {

    @Override
    public GameData getGame(GameData gameData) {
        return null;
    }

    @Override
    public void updateGame(GameData gameData) {

    }

    @Override
    public GameData createGame(GameData gameData) {
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }
}
