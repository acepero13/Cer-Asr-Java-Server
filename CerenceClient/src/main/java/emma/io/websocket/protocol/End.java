package emma.io.websocket.protocol;


import emma.config.ServerConfiguration;
import emma.network.SendableClient;

public class End implements Request {
    private final SendableClient client;

    public End(SendableClient client) {
        this.client = client;
    }

    @Override
    public void write() {
        String data = ServerConfiguration.endingBoundary();
        client.send(Request.lengthOf(data));
        client.send(data);
        client.send(Request.bytesOf("0"));
        client.flush();

    }
}
