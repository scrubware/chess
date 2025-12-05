package handlers;

import chess.ChessMove;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final Gson gson = new Gson();

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx) {
        ctx.enableAutomaticPings();
        System.out.println("Websocket connected");
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {

        var command = gson.fromJson(ctx.message(), UserGameCommand.class);
        var gameID = command.getGameID();
        var auth = command.getAuthToken();

        switch (command.getCommandType()) {
            case CONNECT -> {

            }
            case MAKE_MOVE -> {
                ChessMove move = gson.fromJson(ctx.message(), MakeMoveCommand.class).getMove();
            }
            case LEAVE -> {

            }
            case RESIGN -> {

            }
        }
    }
}
