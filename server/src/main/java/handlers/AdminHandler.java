package handlers;

import dataaccess.*;
import io.javalin.http.Context;

import service.AdminService;

public class AdminHandler {

    AdminService adminService;

    public AdminHandler(ClearDAO clearDAO) {
        adminService = new AdminService(clearDAO);
    }

    public void handleClear(Context ctx) throws DataAccessException {
        adminService.clear();
        ctx.status(200);
    }
}
