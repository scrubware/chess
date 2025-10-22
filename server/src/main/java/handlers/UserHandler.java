package handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import io.javalin.http.Context;

import model.UserData;
import service.UserService;
import requests.RegisterRequest;
import results.RegisterResult;


public class UserHandler {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    private final Gson gson = new Gson();
    private final UserService userService;

    public UserHandler(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
        this.userService = new UserService(userDAO);
    }

    public void handleRegister(Context ctx) {
        UserData userData = gson.fromJson(ctx.body(), UserData.class);
        RegisterResult result = userService.register(userData);
    }

    public void handleLogin(Context ctx) {

    }

    public void handleLogout(Context ctx) {

    }
}
