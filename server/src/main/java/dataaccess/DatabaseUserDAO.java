package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseUserDAO implements UserDAO {
    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            createUserTable(conn);

            var sql = "SELECT username, password, email FROM user WHERE username=?";

            try (var statement = conn.prepareStatement(sql)) {
                statement.setString(1,username);
                try (var rs = statement.executeQuery()) {
                    while (rs.next()) {
                        rs.getString("username");
                        String password = rs.getString("password");
                        String email = rs.getString("email");

                        return new UserData(username, password, email);
                    }
                }
            }

        } catch (Exception _) {
            throw new DataAccessException();
        }

        return null;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {

        try (var conn = DatabaseManager.getConnection()) {
            createUserTable(conn);

            var createUser = "INSERT INTO user (username, password, email) VALUES(?, ?, ?)";

            try (var userStatement = conn.prepareStatement(createUser)) {
                userStatement.setString(1,userData.username());
                userStatement.setString(2,BCrypt.hashpw(userData.password(), BCrypt.gensalt()));
                userStatement.setString(3,userData.email());
                userStatement.executeUpdate();
            }

        } catch(Exception _) {
            throw new DataAccessException();
        }
    }

    private void createUserTable(Connection connection) throws SQLException {
        var userTable = """
            CREATE TABLE IF NOT EXISTS user (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            )""";

        var tableStatement = connection.prepareStatement(userTable);
        tableStatement.executeUpdate();
    }
}
