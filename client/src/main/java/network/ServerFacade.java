package network;

import com.google.gson.Gson;
import exceptions.AlreadyTakenException;
import exceptions.AuthException;
import exceptions.BadRequestException;
import exceptions.InvalidAuthTokenException;
import model.AuthData;
import model.NetworkMessage;
import model.UserData;
import requests.LoginRequest;
import results.CreateGameResult;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ServerFacade {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    private final String address;

    public ServerFacade(int port) {
        address = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email) throws URISyntaxException, IOException, InterruptedException, BadRequestException, AlreadyTakenException, IllegalStateException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(new UserData(username,password,email))))
                .uri(new URI(address + "/user"))
                .timeout(Duration.ofMillis(5000))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return switch (httpResponse.statusCode()) {
            case 200 -> gson.fromJson(httpResponse.body(), AuthData.class);
            case 400 -> throw new BadRequestException();
            case 403 -> throw new AlreadyTakenException();
            default -> throw new IllegalStateException("Unexpected response code: " + httpResponse.statusCode());
        };
    }

    public AuthData login(String username, String password) throws URISyntaxException, IOException, InterruptedException, AuthException, IllegalStateException, BadRequestException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(new LoginRequest(username,password))))
                .uri(new URI(address + "/session"))
                .timeout(Duration.ofMillis(5000))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return switch (httpResponse.statusCode()) {
            case 200 -> gson.fromJson(httpResponse.body(), AuthData.class);
            case 400 -> throw new BadRequestException();
            case 401 -> throw new AuthException(gson.fromJson(httpResponse.body(), NetworkMessage.class).message());
            default -> throw new IllegalStateException("Unexpected response code: " + httpResponse.statusCode());
        };
    }

    public void logout(AuthData auth) throws URISyntaxException, IOException, InterruptedException, InvalidAuthTokenException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(new URI(address + "/session"))
                .header("authorization", auth.authToken())
                .timeout(Duration.ofMillis(5000))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        switch (httpResponse.statusCode()) {
            case 200: return;
            case 401:
                throw new InvalidAuthTokenException();
            default:
                throw new IllegalStateException("Unexpected response code: " + httpResponse.statusCode());
        }
    }

    public int createGame(AuthData auth, String name) throws URISyntaxException, IOException, InterruptedException, InvalidAuthTokenException, IllegalStateException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("{\"gameID\":\"" + name + "\"}"))
                .uri(new URI(address + "/game"))
                .header("authorization", auth.authToken())
                .timeout(Duration.ofMillis(5000))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return switch (httpResponse.statusCode()) {
            case 200 -> gson.fromJson(httpResponse.body(), CreateGameResult.class).gameID();
            case 401 -> throw new InvalidAuthTokenException();
            default -> throw new IllegalStateException("Unexpected response code: " + httpResponse.statusCode());
        };
    }

    public void listGames() {

    }

    public void joinGame() {
    }

    public void observeGame() {

    }
}
