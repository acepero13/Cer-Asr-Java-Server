package emma.io.websocket.protocol;


import emma.config.ServerConfiguration;
import emma.network.SendableClient;

public class End implements Request {
    private final SendableClient client;
    private final ServerConfiguration config;

    public End(SendableClient client, ServerConfiguration configuration) {
        this.client = client;
        this.config = configuration;
    }

    @Override
    public void write() {
        String data = config.endingBoundary();
        client.send(Request.lengthOf(data));
        client.send(data);
        client.send(Request.bytesOf("0"));
        client.flush();

    }
}
