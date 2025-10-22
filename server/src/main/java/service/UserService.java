package service;

import dataaccess.UserDAO;
import model.UserData;
import requests.RegisterRequest;
import results.LoginResult;
import results.RegisterResult;

import java.util.HashSet;

public class UserService
{
    private UserDAO userDAO;
    HashSet<UserData> users;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public RegisterResult register(UserData userData) {

        userDAO.createUser(userData);

        return new RegisterResult("","");
    }

    public LoginResult login(UserData userData) throws UserNotRegisteredException {
        UserData user = userDAO.getUser(userData.username());
        if (user != null) {

        }
    }
}
