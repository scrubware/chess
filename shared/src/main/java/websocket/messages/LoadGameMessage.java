package websocket.messages;

import model.GameData;

import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;

public class LoadGameMessage extends ServerMessage {

    private final GameData game;

    public LoadGameMessage(GameData game) {
        super(LOAD_GAME);
        this.game = game;
    }

    public GameData getGame() {
        return game;
    }
}
