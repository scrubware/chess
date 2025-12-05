package network;

import chess.ChessMove;
import com.google.gson.*;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

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
            public void onMessage(String message) {

            }
        });
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
