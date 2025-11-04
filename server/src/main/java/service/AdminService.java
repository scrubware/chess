package service;


import dataaccess.*;
import requests.ClearRequest;
import results.ClearResult;

public class AdminService {

    private final ClearDAO clearDAO;

    public AdminService(ClearDAO clearDAO) {
        this.clearDAO = clearDAO;
    }

    public void clear() throws DataAccessException {
        clearDAO.clear();
    }
}
