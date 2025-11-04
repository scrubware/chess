package dataaccess;

public class DatabaseClearDAO implements ClearDAO {
    @Override
    public void clear() throws DataAccessException {
        try {
            DatabaseManager.dropDatabase();
        } catch (Exception _) {
            throw new DataAccessException("clear failed");
        }
    }
}
