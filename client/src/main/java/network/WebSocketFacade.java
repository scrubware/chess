package network;

import chess.ChessMove;
import com.google.gson.*;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    public Session session;

    private final Gson gson = new Gson();
    private final String address;

    public WebSocketFacade(int port) {
        address = "ws://localhost:" + port + "/ws";

    }

    public void connect() throws DeploymentException, IOException, URISyntaxException {
        URI uri = new URI(address);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                switch (serverMessage.getServerMessageType()) {
                    case LOAD_GAME -> {
                        var game = gson.fromJson(message, LoadGameMessage.class).getGame();
                    }
                    case ERROR -> {
                        var errorMessage = gson.fromJson(message, ErrorMessage.class).getErrorMessage();
                    }
                    case NOTIFICATION -> {
                        var notificationMessage = gson.fromJson(message, NotificationMessage.class).getMessage();
                        System.out.println(notificationMessage);
                    }
                }
            }
        });
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
