package handlers;

import com.google.gson.Gson;
import dataaccess.UserDAO;
import io.javalin.http.Context;

import model.UserData;
import service.UserService;
import requests.RegisterRequest;
import results.RegisterResult;


public class UserHandler {

    private UserDAO userDAO;

    private final Gson gson = new Gson();
    private final UserService userService = new UserService();

    public UserHandler(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void handleRegister(Context ctx) {
        UserData user = gson.fromJson(ctx.body(), UserData.class);
        RegisterResult result = userService.register(new RegisterRequest(user.username(), user.password(), user.email()));
    }

    public void handleLogin(Context ctx) {

    }

    public void handleLogout(Context ctx) {

    }
}
