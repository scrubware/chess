package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseAuthDAO implements AuthDAO {
    @Override
    public String getUsername(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            createAuthTable(conn);

            String sql = "SELECT username, authToken FROM auth WHERE authToken=?";

            try (var statement = conn.prepareStatement(sql)) {
                statement.setString(1, authToken);
                try (var result = statement.executeQuery()) {
                    result.next();
                    return result.getString("username");
                }
            }
        } catch (Exception _) {
            return null;
        }
    }

    @Override
    public AuthData createAuth(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            createAuthTable(conn);

            String sql = "INSERT INTO auth (username, authToken) VALUES(?, ?)";

            AuthData authData = new AuthData(UUID.randomUUID().toString(),username);

            try (var statement = conn.prepareStatement(sql)) {
                statement.setString(1,authData.username());
                statement.setString(2,authData.authToken());
                statement.executeUpdate();
                return authData;
            }
        } catch (Exception _) {
            return null;
        }
    }

    @Override
    public boolean authExists(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            createAuthTable(conn);

            String sql = "SELECT username, authToken FROM auth WHERE authToken=?";

            try (var statement = conn.prepareStatement(sql)) {
                statement.setString(1, authToken);
                try (var result = statement.executeQuery()) {
                    result.next();
                    boolean user = result.getString("username") != null;
                    boolean auth = result.getString("authToken") != null;
                    return user && auth;
                }
            }
        } catch (Exception _) {
            return false;
        }
    }

    @Override
    public void deleteAuth(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            createAuthTable(conn);

            String sql = "DELETE FROM auth WHERE authToken=?";

            try (var statement = conn.prepareStatement(sql)) {
                statement.setString(1,authToken);
                statement.executeUpdate();
            }
        } catch (Exception _) {

        }
    }

    private void createAuthTable(Connection connection) throws SQLException, DataAccessException {
        var authTable = """
        CREATE TABLE IF NOT EXISTS auth (
            username VARCHAR(255) NOT NULL,
            authToken VARCHAR(255) NOT NULL,
            PRIMARY KEY (username)
        )""";

        var tableStatement = connection.prepareStatement(authTable);
        tableStatement.executeUpdate();

        DatabaseManager.createDatabase();
        DatabaseManager.setCatalog();
    }
}
