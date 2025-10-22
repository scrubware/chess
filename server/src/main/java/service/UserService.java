package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import requests.LoginRequest;
import requests.LogoutRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.RegisterResult;

import java.util.HashSet;
import java.util.Objects;

public class UserService
{
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    HashSet<UserData> users;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public AuthData register(UserData userData) throws UsernameAlreadyTakenException {
        UserData user = userDAO.getUser(userData.username());
        if (user != null) throw new UsernameAlreadyTakenException();
        userDAO.createUser(userData);
        return authDAO.createAuth(userData.username());
    }

    public AuthData login(LoginRequest loginRequest) throws UserNotRegisteredException, PasswordIncorrectException {
        UserData user = userDAO.getUser(loginRequest.username());
        if (user == null) throw new UserNotRegisteredException();
        if (!Objects.equals(loginRequest.password(), user.password())) throw new PasswordIncorrectException();
        return authDAO.createAuth(loginRequest.username());
    }

    public void logout(LogoutRequest logoutRequest) throws InvalidAuthTokenException {
        if (!authDAO.authExists(logoutRequest.authToken())) throw new InvalidAuthTokenException();
        authDAO.deleteAuth(logoutRequest.authToken());
    }
}
