package network;

import com.google.gson.Gson;
import exceptions.*;
import model.AuthData;
import model.GameData;
import model.NetworkMessage;
import model.UserData;
import requests.JoinGameRequest;
import requests.LoginRequest;
import results.CreateGameResult;
import results.ListGamesResult;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collection;

public class ServerFacade {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    private final String address;

    public ServerFacade(int port) {
        address = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email) throws URISyntaxException,
            IOException, InterruptedException, BadRequestException, AlreadyTakenException, IllegalStateException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(new UserData(username,password,email))))
                .uri(new URI(address + "/user"))
                .timeout(Duration.ofMillis(5000))
                .build();

        HttpResponse<String> httpResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        return switch (httpResponse.statusCode()) {
            case 200 -> GSON.fromJson(httpResponse.body(), AuthData.class);
            case 400 -> throw new BadRequestException();
            case 403 -> throw new AlreadyTakenException();
            default -> throw new IllegalStateException("Unexpected response code: " + httpResponse.statusCode());
        };
    }

    public AuthData login(String username, String password) throws URISyntaxException, IOException,
            InterruptedException, AuthException, IllegalStateException, BadRequestException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(new LoginRequest(username,password))))
                .uri(new URI(address + "/session"))
                .timeout(Duration.ofMillis(5000))
                .build();

        HttpResponse<String> httpResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        return switch (httpResponse.statusCode()) {
            case 200 -> GSON.fromJson(httpResponse.body(), AuthData.class);
            case 400 -> throw new BadRequestException();
            case 401 -> throw new AuthException(GSON.fromJson(httpResponse.body(), NetworkMessage.class).message());
            default -> throw new IllegalStateException("Unexpected response code: " + httpResponse.statusCode());
        };
    }

    public void logout(AuthData auth) throws URISyntaxException, IOException, InterruptedException,
            InvalidAuthTokenException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(new URI(address + "/session"))
                .header("authorization", auth.authToken())
                .timeout(Duration.ofMillis(5000))
                .build();

        HttpResponse<String> httpResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        switch (httpResponse.statusCode()) {
            case 200: return;
            case 401:
                throw new InvalidAuthTokenException();
            default:
                throw new IllegalStateException("Unexpected response code: " + httpResponse.statusCode());
        }
    }

    public int createGame(AuthData auth, String name) throws URISyntaxException, IOException,
            InterruptedException, InvalidAuthTokenException, IllegalStateException, BadRequestException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("{\"gameName\":\"" + name + "\"}"))
                .uri(new URI(address + "/game"))
                .header("authorization", auth.authToken())
                .timeout(Duration.ofMillis(5000))
                .build();

        HttpResponse<String> httpResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        return switch (httpResponse.statusCode()) {
            case 200 -> GSON.fromJson(httpResponse.body(), CreateGameResult.class).gameID();
            case 400 -> throw new BadRequestException();
            case 401 -> throw new InvalidAuthTokenException();
            default -> throw new IllegalStateException("Unexpected response code: " + httpResponse.statusCode());
        };
    }

    public Collection<GameData> listGames(AuthData auth) throws URISyntaxException, IOException,
            InterruptedException, InvalidAuthTokenException, IllegalStateException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(new URI(address + "/game"))
                .header("authorization", auth.authToken())
                .timeout(Duration.ofMillis(5000))
                .build();

        HttpResponse<String> httpResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        return switch (httpResponse.statusCode()) {
            case 200 -> GSON.fromJson(httpResponse.body(), ListGamesResult.class).games();
            case 401 -> throw new InvalidAuthTokenException();
            default -> throw new IllegalStateException("Unexpected response code: " + httpResponse.statusCode());
        };
    }

    public void joinGame(AuthData auth, String color, int gameID) throws URISyntaxException,
            IOException, InterruptedException, InvalidGameIDException {
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(GSON.toJson(new JoinGameRequest(color,gameID))))
                .uri(new URI(address + "/game"))
                .header("authorization", auth.authToken())
                .timeout(Duration.ofMillis(5000))
                .build();

        HttpResponse<String> httpResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        switch (httpResponse.statusCode()) {
            case 200: return;
            case 401: throw new InvalidAuthTokenException();
            case 403: throw new LockedGameException();
            case 500: throw new InvalidGameIDException();
            default: throw new IllegalStateException("Unexpected response code: " + httpResponse.statusCode());
        }
    }
}
