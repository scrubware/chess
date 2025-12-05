package websocket.messages;

import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;

public class NotificationMessage extends ServerMessage {

    private final String message;

    public NotificationMessage(String message) {
        super(NOTIFICATION);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
