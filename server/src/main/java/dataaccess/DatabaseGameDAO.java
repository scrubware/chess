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
    public GameData getGame(int gameID) throws DataAccessException {
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

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean updateGame(int gameID, GameData gameData) {
        try (var conn = DatabaseManager.getConnection()) {
            createGameTable(conn);

            String sql = "UPDATE game SET white=?, black=?, gname=?, gdata=? WHERE id=?";

            try (var statement = conn.prepareStatement(sql)) {
                statement.setString(1,gameData.whiteUsername());
                statement.setString(2,gameData.blackUsername());
                statement.setString(3,gameData.gameName());
                statement.setString(4,gson.toJson(gameData.game()));
                statement.setInt(5,gameID);

                int rows = statement.executeUpdate();

                // This condition runs if 'WHERE id=?;' call failed.
                if (rows == 0) {
                    throw new SQLException();
                }

                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int createGame(String name) {
        try (var conn = DatabaseManager.getConnection()) {
            createGameTable(conn);

            String sql = "INSERT INTO game (white, black, gname, gdata, locked) VALUES (?,?,?,?,?)";

            try (var statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1,null);
                statement.setString(2,null);
                statement.setString(3,name);
                statement.setString(4,gson.toJson(new ChessGame()));
                statement.setBoolean(5,false);

                statement.executeUpdate();

                var resultSet = statement.getGeneratedKeys();
                var id = -1;
                if (resultSet.next()) {
                    id = resultSet.getInt(1);
                }

                return id;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public void removeGame(int gameID) {
        try (var conn = DatabaseManager.getConnection()) {

            String sql = "DELETE FROM game WHERE id=?";

            var statement = conn.prepareStatement(sql);
            statement.setInt(1,gameID);
            statement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lockGame(int gameID) {
        try (var conn = DatabaseManager.getConnection()) {
            createGameTable(conn);

            String sql = "UPDATE game SET locked=? WHERE id=?";

            try (var statement = conn.prepareStatement(sql)) {
                statement.setBoolean(1, true);
                statement.setInt(2,gameID);

                int rows = statement.executeUpdate();

                // This condition runs if 'WHERE id=?;' call failed.
                if (rows == 0) {
                    throw new SQLException();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean getGameLocked(int gameID) {
        try (var conn = DatabaseManager.getConnection()) {
            createGameTable(conn);

            String sql = "SELECT locked FROM game WHERE id=?";

            try (var statement = conn.prepareStatement(sql)) {
                statement.setInt(1,gameID);
                var result = statement.executeQuery();

                result.next();
                boolean r = result.getBoolean("locked");

                return r;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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

        } catch (Exception e) {
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
                locked TINYINT(1),
                PRIMARY KEY (id)
            )""";

        var tableStatement = connection.prepareStatement(userTable);
        tableStatement.executeUpdate();
    }
}
