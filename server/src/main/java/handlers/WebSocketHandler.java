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
import websocket.messages.*;

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

        String username = authDAO.getUsername(auth);
        GameData game = gameDAO.getGame(gameID);

        if (!authDAO.authExists(auth)) {
            throw new InvalidAuthTokenException();
        }

        switch (command.getCommandType()) {
            case CONNECT -> {
                clients.add(ctx.session);
                if (username.equals(game.whiteUsername())) {
                    sendToAllClients(new NotificationMessage(username + " joined the game as WHITE"));
                } else if (username.equals(game.blackUsername())) {
                    sendToAllClients(new NotificationMessage(username + " joined the game as BLACK"));
                } else {
                    sendToAllClients(new NotificationMessage(username + " is now watching the game"));
                }

                sendToAllClients(new LoadDataMessage(game));
            }
            case MAKE_MOVE -> {
                ChessMove move = gson.fromJson(ctx.message(), MakeMoveCommand.class).getMove();
                System.out.println(move);

                try {
                    game.game().makeMove(move);
                } catch (InvalidMoveException e) {
                    sendToClient(new ErrorMessage("Error! " + e.getMessage()), ctx.session);
                    break;
                }

                gameDAO.updateGame(gameID,game);

                // Send move notification
                sendToAllClients(new LoadGameMessage(game));
                sendToAllClientsExcept(new NotificationMessage(""),ctx.session);

                if (game.game().isInCheck(BLACK)) {
                    sendToAllClients(new NotificationMessage(game.blackUsername() + " is in check."));
                }

                if (game.game().isInCheck(WHITE)) {
                    sendToAllClients(new NotificationMessage(game.whiteUsername() + " is in check."));
                }

                if (game.game().isInCheckmate(BLACK)) {
                    sendToAllClients(new NotificationMessage(game.blackUsername() + " is in checkmate!"));
                }

                if (game.game().isInCheckmate(WHITE)) {
                    sendToAllClients(new NotificationMessage(game.whiteUsername() + " is in checkmate!"));
                    sendToAllClients(new GameCompleteMessage(null));
                    gameDAO.removeGame(game.gameID());
                }

                if (game.game().isInStalemate(WHITE) || game.game().isInStalemate(BLACK)) {
                    sendToAllClients(new NotificationMessage("The game has reached a stalemate!"));
                    sendToAllClients(new GameCompleteMessage(null));
                    gameDAO.removeGame(game.gameID());
                }
            }
            case LEAVE -> {
                clients.remove(ctx.session);
                if (username.equals(game.whiteUsername())) {
                    sendToAllClients(new NotificationMessage(username + " (WHITE) has left the game!"));
                    var newGame = new GameData(game.gameID(),null,
                                        game.blackUsername(),game.gameName(),game.game());
                    gameDAO.updateGame(gameID,newGame);
                } else if (username.equals(game.blackUsername())) {
                    sendToAllClients(new NotificationMessage(username + " (BLACK) has left the game!"));
                    var newGame = new GameData(game.gameID(),game.whiteUsername(),
                            null,game.gameName(),game.game());
                    gameDAO.updateGame(gameID,newGame);
                } else {
                    sendToAllClients(new NotificationMessage(username + " has stopped watching the game"));
                }

                sendToAllClients(new LoadDataMessage(game));
            }
            case RESIGN -> {
                sendToAllClients(new NotificationMessage(username + " resigned."));

                if (username.equals(game.whiteUsername())) {
                    sendToAllClients(new NotificationMessage(game.blackUsername() + " is victorious!"));
                } else if (username.equals(game.blackUsername())) {
                    sendToAllClients(new NotificationMessage(game.whiteUsername() + " is victorious!"));
                }
            }
        }
    }
}
