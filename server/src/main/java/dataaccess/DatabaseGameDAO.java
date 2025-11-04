package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.util.Collection;
import java.util.List;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseGameDAO implements GameDAO {
    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public boolean updateGame(int gameID, GameData gameData) {
        return false;
    }

    @Override
    public int createGame(String name) {
        try (var conn = DatabaseManager.getConnection()) {
            createGameTable(conn);

            String sql = "INSERT INTO game (white, black, gname, game) VALUES (?,?,?,?)";

            try (var statement = conn.prepareStatement(sql)) {
                statement.setString(1,null);
                statement.setString(2,null);
                statement.setString(3,name);
                statement.setString(4,(new Gson()).toJson(new ChessGame()));

                statement.executeUpdate();

                var resultSet = statement.getGeneratedKeys();
                var id = 0;
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
        return List.of();
    }

    private void createGameTable(Connection connection) throws SQLException {
        var userTable = """
            CREATE TABLE IF NOT EXISTS game (
                id INT NOT NULL AUTO_INCREMENT,
                white VARCHAR(255),
                black VARCHAR(255),
                gname VARCHAR(255) NOT NULL,
                game longtext NOT NULL
                PRIMARY KEY (id)
            )""";

        var tableStatement = connection.prepareStatement(userTable);
        tableStatement.executeUpdate();
    }
}
