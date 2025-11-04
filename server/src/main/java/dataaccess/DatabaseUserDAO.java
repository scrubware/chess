package dataaccess;

import model.UserData;

public class DatabaseUserDAO implements UserDAO {
    @Override
    public UserData getUser(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            var sql = "SELECT username, password, email FROM user WHERE username=?";

            try (var statement = conn.prepareStatement(sql)) {
                statement.setString(1,username);
                try (var rs = statement.executeQuery()) {

                    String password;
                    String email;
                    while (rs.next()) {
                        rs.getString("username");
                        password = rs.getString("password");
                        email = rs.getString("email");

                        return new UserData(username, password, email);
                    }
                }
            }

        } catch (Exception _) {
            return null;
        }

        return null;
    }

    @Override
    public void createUser(UserData userData) {

        try (var conn = DatabaseManager.getConnection()) {
            DatabaseManager.createDatabase();
            DatabaseManager.setCatalog();

            var userTable = """
            CREATE TABLE  IF NOT EXISTS user (
                id INT NOT NULL AUTO_INCREMENT,
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (id)
            )""";


            try (var tableStatement = conn.prepareStatement(userTable)) {
                tableStatement.executeUpdate();
            }

            var createUser = "INSERT INTO user (username, password, email) VALUES(?, ?, ?)";

            try (var userStatement = conn.prepareStatement(createUser)) {
                userStatement.setString(1,userData.username());
                userStatement.setString(2,userData.password());
                userStatement.setString(3,userData.email());
                userStatement.executeUpdate();
            }

        } catch(Exception _) {

        }
    }
}
