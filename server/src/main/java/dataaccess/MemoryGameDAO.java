package dataaccess;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessGame;
import model.GameData;

public class MemoryGameDAO implements GameDAO {

    ArrayList<GameData> games = new ArrayList<>();

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID - 1);
    }

    @Override
    public boolean updateGame(int gameID, GameData gameData) {
        games.set(gameID - 1, gameData);
        return true;
    }

    @Override
    public int createGame(String name) {

        int index = games.size() + 1;

        // Find the earliest unused gameID
        for (int i = 0, n = games.size(); i < n; i ++) {
            if (games.get(i) == null) {
                index = i + 1;
                break;
            }
        }

        games.add(new GameData(index,null, null,name, new ChessGame()));
        return index;
    }

    @Override
    public void removeGame(int gameID) {
        games.remove(gameID - 1);
    }

    @Override
    public void lockGame(int gameID) {

    }

    @Override
    public boolean getGameLocked(int gameID) {
        return false;
    }

    @Override
    public Collection<GameData> listGames() {
        return games;
    }

    public void clear() {
        games.clear();
    }
}
