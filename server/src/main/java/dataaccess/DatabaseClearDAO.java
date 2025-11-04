package dataaccess;

public class DatabaseClearDAO implements ClearDAO {
    @Override
    public void clear() {
        try {
            DatabaseManager.dropDatabase();
        } catch (Exception _) {
        }
    }
}
