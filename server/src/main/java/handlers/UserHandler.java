package handlers;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import io.javalin.http.Context;

import model.AuthData;
import model.UserData;
import service.UserService;

public class UserHandler {

    private final Gson gson = new Gson();
    private final UserService userService;

    public UserHandler(AuthDAO authDAO, UserDAO userDAO) {
        this.userService = new UserService(authDAO, userDAO);
    }

    public void handleRegister(Context ctx) {
        UserData userData = gson.fromJson(ctx.body(), UserData.class);
        AuthData authData = userService.register(userData);
    }

    public void handleLogin(Context ctx) {

    }

    public void handleLogout(Context ctx) {

    }
}
