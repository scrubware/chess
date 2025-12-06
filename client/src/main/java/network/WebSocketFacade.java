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

import static websocket.commands.UserGameCommand.CommandType.*;

public class WebSocketFacade extends Endpoint {

    public Session session;

    private final Gson gson = new Gson();
    private final String address;
    private final Client client;

    public WebSocketFacade(int port, Client client) {
        address = "ws://localhost:" + port + "/ws";
        this.client = client;
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public void connect(String authToken, int gameID) throws DeploymentException, IOException, URISyntaxException {
        URI uri = new URI(address);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(this, uri);

        session.getBasicRemote().sendText(gson.toJson(new UserGameCommand(CONNECT,authToken,gameID)));

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                switch (serverMessage.getServerMessageType()) {
                    case LOAD_GAME -> {
                        var game = gson.fromJson(message, LoadGameMessage.class).getGame();
                        client.updateGame(game);
                        client.drawBoard(null);
                        client.outputUserCursor();
                    }
                    case ERROR -> {
                        var errorMessage = gson.fromJson(message, ErrorMessage.class).getErrorMessage();
                        System.out.println();
                        System.out.println(errorMessage);
                        System.out.println();
                        client.outputUserCursor();
                    }
                    case NOTIFICATION -> {
                        var notificationMessage = gson.fromJson(message, NotificationMessage.class).getMessage();
                        System.out.println();
                        System.out.println(notificationMessage);
                        System.out.println();
                        client.outputUserCursor();
                    }
                }
            }
        });
    }

    public void sendResign(String authToken, int gameID) throws IOException {
        session.getBasicRemote().sendText(gson.toJson(new UserGameCommand(RESIGN,authToken,gameID)));
    }

    public void sendLeave(String authToken, int gameID) throws IOException {
        session.getBasicRemote().sendText(gson.toJson(new UserGameCommand(LEAVE,authToken,gameID)));
    }

    public void sendMove(String authToken, int gameID, ChessMove move) throws IOException {
        session.getBasicRemote().sendText(gson.toJson(new MakeMoveCommand(authToken,gameID,move)));
    }
}
