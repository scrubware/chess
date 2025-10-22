package service;


import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import requests.ClearRequest;
import results.ClearResult;

public class AdminService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public AdminService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public void clear(ClearRequest clearRequest) {
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
    }
}
