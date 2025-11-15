package network;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;

import java.net.URI;
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

    public AuthData register(String username, String password, String email) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(new UserData(username,password,email))))
                    .uri(new URI(address + "/user"))
                    //.header("Authorization", "secret1")
                    .timeout(java.time.Duration.ofMillis(5000))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() >= 200) {
                return gson.fromJson(httpResponse.body(), AuthData.class);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public AuthData login(String username, String password) {
        return null;
    }

    public void logout() {

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
