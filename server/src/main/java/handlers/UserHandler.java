package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;

import requests.LoginRequest;
import service.BadRequestException;
import service.UserService;

import model.AuthData;
import model.UserData;


public class UserHandler {

    private final Gson gson = new Gson();
    private final UserService userService;

    public UserHandler(AuthDAO authDAO, UserDAO userDAO) {
        this.userService = new UserService(authDAO, userDAO);
    }

    public void handleRegister(Context ctx) throws DataAccessException {
        UserData userData = gson.fromJson(ctx.body(), UserData.class);

        if (userData.username() == null || userData.password() == null || userData.email() == null) {
            throw new BadRequestException();
        }

        AuthData authData = userService.register(userData);

        ctx.status(200);
        ctx.result(gson.toJson(authData));
    }

    public void handleLogin(Context ctx) throws DataAccessException {
        LoginRequest loginRequest = gson.fromJson(ctx.body(), LoginRequest.class);

        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new BadRequestException();
        }

        AuthData authData = userService.login(loginRequest);

        ctx.status(200);
        ctx.result(gson.toJson(authData));
    }

    public void handleLogout(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        if (authToken == null) {
            throw new BadRequestException();
        }

        userService.logout(authToken);

        ctx.status(200);
    }
}
