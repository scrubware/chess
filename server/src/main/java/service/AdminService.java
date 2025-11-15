package service;


import dataaccess.*;

public class AdminService {

    private final ClearDAO clearDAO;

    public AdminService(ClearDAO clearDAO) {
        this.clearDAO = clearDAO;
    }

    public void clear() throws DataAccessException {
        clearDAO.clear();
    }
}
