package handlers;

import com.google.gson.Gson;
import io.javalin.http.Context;
import java.util.Map;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;

import service.UserService;
import service.UsernameAlreadyTakenException;

import model.AuthData;
import model.UserData;


public class UserHandler {

    private final Gson gson = new Gson();
    private final UserService userService;

    public UserHandler(AuthDAO authDAO, UserDAO userDAO) {
        this.userService = new UserService(authDAO, userDAO);
    }

    public void handleRegister(Context ctx) {
        UserData userData = gson.fromJson(ctx.body(), UserData.class);
        AuthData authData = userService.register(userData);

        ctx.status(200);
        ctx.result(gson.toJson(authData));
    }

    public void handleLogin(Context ctx) {

    }

    public void handleLogout(Context ctx) {

    }
}
