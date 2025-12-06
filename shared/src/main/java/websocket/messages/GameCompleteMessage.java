package websocket.messages;

import static websocket.messages.ServerMessage.ServerMessageType.GAME_COMPLETE;

public class GameCompleteMessage extends ServerMessage {

    private final String victor;

    public GameCompleteMessage(String victor) {
        super(GAME_COMPLETE);
        this.victor = victor;
    }

    public String getVictor() {
        return victor;
    }
}
