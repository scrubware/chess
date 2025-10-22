package handlers;

import dataaccess.AdminDAO;
import io.javalin.http.Context;
import requests.ClearRequest;
import service.AdminService;

public class AdminHandler {

    AdminService adminService;

    AdminDAO adminDAO;

    public AdminHandler(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;

        adminService = new AdminService();
    }

    public void handleClear(Context ctx) {

        adminService.clear(new ClearRequest());
        ctx.status(200);
    }
}
