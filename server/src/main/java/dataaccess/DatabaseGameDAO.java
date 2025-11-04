package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseGameDAO implements GameDAO {

    Gson gson = new Gson();

    @Override
    public GameData getGame(int gameID) {
        try (var conn = DatabaseManager.getConnection()) {
            createGameTable(conn);

            String sql = "SELECT id, white, black, gname, gdata FROM game WHERE id=?";

            try (var statement = conn.prepareStatement(sql)) {
                statement.setInt(1,gameID);
                var result = statement.executeQuery();

                result.next();
                String white = result.getString("white");
                String black = result.getString("black");
                String gname = result.getString("gname");
                ChessGame gdata = gson.fromJson(result.getString("gdata"),ChessGame.class);

                return new GameData(gameID, white, black, gname, gdata);
            }

        } catch (Exception _) {
            return null;
        }
    }

    @Override
    public boolean updateGame(int gameID, GameData gameData) {
        return false;
    }

    @Override
    public int createGame(String name) {
        try (var conn = DatabaseManager.getConnection()) {
            createGameTable(conn);

            String sql = "INSERT INTO game (white, black, gname, gdata) VALUES (?,?,?,?)";

            try (var statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1,null);
                statement.setString(2,null);
                statement.setString(3,name);
                statement.setString(4,gson.toJson(new ChessGame()));

                statement.executeUpdate();

                var resultSet = statement.getGeneratedKeys();
                var id = -1;
                if (resultSet.next()) {
                    id = resultSet.getInt(1);
                }

                return id;
            }
        } catch (Exception _) {
            return -1;
        }
    }

    @Override
    public Collection<GameData> listGames() {
        try (var conn = DatabaseManager.getConnection()) {
            createGameTable(conn);

            String sql = "SELECT id, white, black, gname, gdata FROM game";

            try (var statement = conn.prepareStatement(sql)) {
                var result = statement.executeQuery();

                List<GameData> games = new ArrayList<>();

                while (result.next()) {
                    int id = result.getInt(1);
                    String white = result.getString(2);
                    String black = result.getString(3);
                    String name = result.getString(4);
                    ChessGame game = gson.fromJson(result.getString(5),ChessGame.class);

                    games.add(new GameData(id, white, black, name, game));
                }

                return games;
            }

        } catch (Exception _) {
            return new ArrayList<>();
        }
    }

    private void createGameTable(Connection connection) throws SQLException {
        var userTable = """
            CREATE TABLE IF NOT EXISTS game (
                id INT NOT NULL AUTO_INCREMENT,
                white VARCHAR(255),
                black VARCHAR(255),
                gname VARCHAR(255) NOT NULL,
                gdata longtext NOT NULL,
                PRIMARY KEY (id)
            )""";

        var tableStatement = connection.prepareStatement(userTable);
        tableStatement.executeUpdate();
    }
}
