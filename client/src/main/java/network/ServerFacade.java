package network;

import com.google.gson.Gson;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.UnknownException;
import model.AuthData;
import model.UserData;
import requests.LoginRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    private final String address;

    public ServerFacade(int port) {
        address = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email) throws URISyntaxException, IOException, InterruptedException, BadRequestException, AlreadyTakenException, UnknownException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(new UserData(username,password,email))))
                .uri(new URI(address + "/user"))
                .timeout(java.time.Duration.ofMillis(5000))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() == 200) {
            return gson.fromJson(httpResponse.body(), AuthData.class);
        }
        switch (httpResponse.statusCode()) {
            case 200:
                return gson.fromJson(httpResponse.body(), AuthData.class);
            case 400:
                throw new BadRequestException();
            case 403:
                throw new AlreadyTakenException();
            default:
                throw new UnknownException();
        }
    }

    public AuthData login(String username, String password) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(new LoginRequest(username,password))))
                    .uri(new URI(address + "/session"))
                    .timeout(java.time.Duration.ofMillis(5000))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() == 200) {
                return gson.fromJson(httpResponse.body(), AuthData.class);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void logout(AuthData auth) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .DELETE()
                    .uri(new URI(address + "/session"))
                    .header("authorization", auth.authToken())
                    .timeout(java.time.Duration.ofMillis(5000))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() == 200) {
                //return gson.fromJson(httpResponse.body(), AuthData.class);
            }
            //return null;
        } catch (Exception e) {
            //return null;
        }
    }

    public void createGame() {

    }

    public void listGames() {

    }

    public void joinGame() {
    }

    public void observeGame() {

    }
}
