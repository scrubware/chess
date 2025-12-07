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
import java.util.HashMap;
import java.util.HashSet;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final HashMap<Integer, HashSet<Session>> clients = new HashMap<>();

    private final Gson gson = new Gson();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    private void sendToAllClients(int gameID, ServerMessage message) throws IOException {
        String serializedString = gson.toJson(message);

        for (Session s : clients.get(gameID)) {
            if (s.isOpen()) {
                s.getRemote().sendString(serializedString);
            }
        }
    }

    private void sendToAllClientsExcept(int gameID, ServerMessage message, Session except) throws IOException {
        String serializedString = gson.toJson(message);

        for (Session s : clients.get(gameID)) {
            if (s.isOpen() && !s.equals(except)) {
                s.getRemote().sendString(serializedString);
            }
        }
    }

    private void sendToClient(ServerMessage message, Session session) throws IOException {
        String serializedString = gson.toJson(message);
        session.getRemote().sendString(serializedString);
    }

    private void handleResign(Session session, GameData game, int gameID, String username) throws IOException {

        if (gameDAO.getGameLocked(gameID)) {
            sendToClient(new ErrorMessage("Error! The game has already completed."), session);
            return;
        }

        if (!(username.equals(game.whiteUsername()) || username.equals(game.blackUsername()))) {
            sendToClient(new ErrorMessage("Error! You cannot resign as an observer."), session);
            return;
        }

        sendToAllClients(gameID, new NotificationMessage(username + " resigned."));

        gameDAO.lockGame(game.gameID());
    }

    private void handleMove(Session session, String message, GameData game, int gameID, String username)
            throws IOException, DataAccessException {
        ChessMove move = gson.fromJson(message, MakeMoveCommand.class).getMove();
        System.out.println(move);

        if (gameDAO.getGameLocked(game.gameID())) {
            sendToClient(new ErrorMessage("Error! Game has already completed."), session);
            return;
        }

        if (!(username.equals(game.whiteUsername()) || username.equals(game.blackUsername()))) {
            sendToClient(new ErrorMessage("Error! You cannot make moves as an observer."), session);
            return;
        }

        var piece = game.game().getBoard().getPiece(move.getEndPosition());
        if (username.equals(game.whiteUsername()) && piece != null && piece.getTeamColor() != WHITE) {
            sendToClient(new ErrorMessage("Error! You move the opponent's pieces."), session);
            return;
        } else if (username.equals(game.blackUsername()) && piece != null && piece.getTeamColor() != BLACK) {
            sendToClient(new ErrorMessage("Error! You move the opponent's pieces."), session);
            return;
        }

        if (username.equals(game.whiteUsername()) && game.game().getTeamTurn() != WHITE) {
            sendToClient(new ErrorMessage("Error! You cannot move on the opponent's turn."), session);
            return;
        } else if (username.equals(game.blackUsername()) && game.game().getTeamTurn() != BLACK) {
            sendToClient(new ErrorMessage("Error! You cannot move on the opponent's turn."), session);
            return;
        }

        try {
            game.game().makeMove(move);
        } catch (InvalidMoveException e) {
            sendToClient(new ErrorMessage("Error! " + e.getMessage()), session);
            return;
        }

        gameDAO.updateGame(gameID,game);

        // Send move notification
        sendToAllClients(gameID, new LoadGameMessage(game));
        sendToAllClientsExcept(gameID, new NotificationMessage(""),session);

        if (game.game().isInCheckmate(BLACK)) {
            sendToAllClients(gameID, new NotificationMessage(game.blackUsername() + " is in checkmate!"));
            //sendToAllClients(gameID, new GameCompleteMessage(game.whiteUsername()));
            gameDAO.lockGame(game.gameID());
        } else if (game.game().isInCheck(BLACK)) {
            sendToAllClients(gameID, new NotificationMessage(game.blackUsername() + " is in check."));
        }

        if (game.game().isInCheckmate(WHITE)) {
            sendToAllClients(gameID, new NotificationMessage(game.whiteUsername() + " is in checkmate!"));
            //sendToAllClients(gameID, new GameCompleteMessage(game.blackUsername()));
            gameDAO.lockGame(game.gameID());
        } else if (game.game().isInCheck(WHITE)) {
            sendToAllClients(gameID, new NotificationMessage(game.whiteUsername() + " is in check."));
        }

        if (game.game().isInStalemate(WHITE) || game.game().isInStalemate(BLACK)) {
            sendToAllClients(gameID, new NotificationMessage("The game has reached a stalemate!"));
            //sendToAllClients(gameID, new GameCompleteMessage(null));
            gameDAO.lockGame(game.gameID());
        }
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
            sendToClient(new ErrorMessage("Error! Login session has expired."), ctx.session);
            return;
        }

        if (!clients.containsKey(gameID)) {
            clients.put(gameID, new HashSet<>());
        }

        switch (command.getCommandType()) {
            case CONNECT -> {

                if (game == null) {
                    sendToClient(new ErrorMessage("Error! Invalid game index."), ctx.session);
                    return;
                }

                sendToClient(new LoadGameMessage(game), ctx.session);

                clients.get(gameID).add(ctx.session);
                if (username.equals(game.whiteUsername())) {
                    sendToAllClientsExcept(gameID, new NotificationMessage(username + " joined the game as WHITE"), ctx.session);
                } else if (username.equals(game.blackUsername())) {
                    sendToAllClientsExcept(gameID, new NotificationMessage(username + " joined the game as BLACK"), ctx.session);
                } else {
                    sendToAllClientsExcept(gameID, new NotificationMessage(username + " is now watching the game"), ctx.session);
                }

                //sendToAllClients(gameID, new LoadDataMessage(game));
            }
            case MAKE_MOVE -> handleMove(ctx.session, ctx.message(), game, gameID, username);
            case LEAVE -> {
                clients.get(gameID).remove(ctx.session);
                if (username.equals(game.whiteUsername())) {
                    sendToAllClients(gameID, new NotificationMessage(username + " (WHITE) has left the game!"));
                    var newGame = new GameData(game.gameID(),null,
                                        game.blackUsername(),game.gameName(),game.game());
                    gameDAO.updateGame(gameID,newGame);
                } else if (username.equals(game.blackUsername())) {
                    sendToAllClients(gameID, new NotificationMessage(username + " (BLACK) has left the game!"));
                    var newGame = new GameData(game.gameID(),game.whiteUsername(),
                            null,game.gameName(),game.game());
                    gameDAO.updateGame(gameID,newGame);
                } else {
                    sendToAllClients(gameID, new NotificationMessage(username + " has stopped watching the game"));
                }

                //sendToAllClients(gameID, new LoadDataMessage(game));
            }
            case RESIGN -> handleResign(ctx.session,game,gameID,username);
        }
    }
}
