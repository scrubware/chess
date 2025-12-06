package handlers;

import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exceptions.InvalidAuthTokenException;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashSet;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final HashSet<Session> clients = new HashSet<>();

    private final Gson gson = new Gson();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    private void sendToAllClients(ServerMessage message) throws IOException {
        String serializedString = gson.toJson(message);

        for (Session s : clients) {
            if (s.isOpen()) {
                s.getRemote().sendString(serializedString);
            }
        }
    }

    private void sendToAllClientsExcept(ServerMessage message, Session except) throws IOException {
        String serializedString = gson.toJson(message);

        for (Session s : clients) {
            if (s.isOpen() && !s.equals(except)) {
                s.getRemote().sendString(serializedString);
            }
        }
    }

    private void sendToClient(ServerMessage message, Session session) throws IOException {
        String serializedString = gson.toJson(message);
        session.getRemote().sendString(serializedString);
    }

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
    public void handleMessage(@NotNull WsMessageContext ctx) throws DataAccessException, IOException {

        var command = gson.fromJson(ctx.message(), UserGameCommand.class);
        var gameID = command.getGameID();
        var auth = command.getAuthToken();

        if (!authDAO.authExists(auth)) {
            throw new InvalidAuthTokenException();
        }

        switch (command.getCommandType()) {
            case CONNECT -> {
                clients.add(ctx.session);
            }
            case MAKE_MOVE -> {
                ChessMove move = gson.fromJson(ctx.message(), MakeMoveCommand.class).getMove();
                System.out.println(move);

                var game = gameDAO.getGame(gameID);

                try {
                    game.game().makeMove(move);
                } catch (InvalidMoveException e) {
                    sendToClient(new ErrorMessage("Error! That is not a valid move!"), ctx.session);
                    break;
                }

                gameDAO.updateGame(gameID,game);

                // Docs suggest doing the checking before sending messages.
                if (game.game().isInCheckmate(BLACK)) {
                    // Send black is in checkmate notification
                }

                if (game.game().isInCheckmate(WHITE)) {

                }

                if (game.game().isInStalemate(WHITE) || game.game().isInStalemate(BLACK)) {

                }

                // Send move notification
                sendToAllClients(new LoadGameMessage(game));
                sendToAllClientsExcept(new NotificationMessage(""),ctx.session);
            }
            case LEAVE -> {
                clients.remove(ctx.session);
            }
            case RESIGN -> {

            }
        }
    }
}
