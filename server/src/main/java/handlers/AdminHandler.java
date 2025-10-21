package handlers;

import io.javalin.http.Context;
import requests.ClearRequest;
import service.AdminService;

public class AdminHandler {

    AdminService adminService;

    public AdminHandler() {
        adminService = new AdminService();
    }

    public void handleClear(Context ctx) {

        adminService.clear(new ClearRequest());
        ctx.status(200);
    }
}
