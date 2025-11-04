package dataaccess;

import model.UserData;

public class DatabaseUserDAO implements UserDAO {
    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createUser(UserData userData) {

        try (var conn = DatabaseManager.getConnection()) {
            DatabaseManager.createDatabase();
            DatabaseManager.setCatalog();

            var createUserTable = """
            CREATE TABLE  IF NOT EXISTS user (
                id INT NOT NULL AUTO_INCREMENT,
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                PRIMARY KEY (id)
            )""";


            try (var createTableStatement = conn.prepareStatement(createUserTable)) {
                createTableStatement.executeUpdate();
            }

        } catch(Exception _) {

        }
    }
}
