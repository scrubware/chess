package network;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    public Session session;

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
                // This is where messages are received by the client.
            }
        });
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
