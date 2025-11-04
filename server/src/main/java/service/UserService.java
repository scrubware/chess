package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import requests.LoginRequest;

public class UserService
{
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public AuthData register(UserData userData) throws AlreadyTakenException, DataAccessException {
        UserData user = userDAO.getUser(userData.username());
        if (user != null) {
            throw new AlreadyTakenException();
        }
        userDAO.createUser(userData);
        return authDAO.createAuth(userData.username());
    }

    public AuthData login(LoginRequest loginRequest) throws UserNotRegisteredException, PasswordIncorrectException, DataAccessException {
        UserData user = userDAO.getUser(loginRequest.username());
        if (user == null) {
            throw new UserNotRegisteredException();
        }
        if (!BCrypt.checkpw(loginRequest.password(),user.password())) {
            throw new PasswordIncorrectException();
        }
        return authDAO.createAuth(loginRequest.username());
    }

    public void logout(String authToken) throws InvalidAuthTokenException, DataAccessException {
        if (!authDAO.authExists(authToken)) {
            throw new InvalidAuthTokenException();
        }
        authDAO.deleteAuth(authToken);
    }
}
