package websocket.messages;

import model.GameData;

import static websocket.messages.ServerMessage.ServerMessageType.LOAD_DATA;

public class LoadDataMessage extends ServerMessage {

    private final GameData game;

    public LoadDataMessage(GameData game) {
        super(LOAD_DATA);
        this.game = game;
    }

    public GameData getGame() {
        return game;
    }
}
