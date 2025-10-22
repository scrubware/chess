package dataaccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import chess.ChessGame;
import model.GameData;

public class MemoryGameDAO implements GameDAO {

    ArrayList<GameData> games = new ArrayList<>();

    @Override
    public GameData getGame(int game_id) {
        return null;
    }

    @Override
    public void updateGame(GameData gameData) {

    }

    @Override
    public int createGame(String name) {

        int index = games.size();

        // Find the earliest unused gameID
        for (int i = 0, n = games.size(); i < n; i ++) {
            if (games.get(i) == null) {
                index = i;
                break;
            }
        }

        games.add(new GameData(index,"","",name, new ChessGame()));
        return index;
    }

    @Override
    public Collection<GameData> listGames() {
        return games;
    }

    @Override
    public void clear() {

    }
}
