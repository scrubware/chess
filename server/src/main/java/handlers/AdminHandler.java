package handlers;

import io.javalin.http.Context;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

import requests.ClearRequest;

import service.AdminService;

public class AdminHandler {

    AdminService adminService;

    public AdminHandler(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        adminService = new AdminService(authDAO, gameDAO, userDAO);
    }

    public void handleClear(Context ctx) {
        adminService.clear();
        ctx.status(200);
    }
}
