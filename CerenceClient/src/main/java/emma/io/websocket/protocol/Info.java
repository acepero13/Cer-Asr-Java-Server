package emma.io.websocket.protocol;


import emma.config.ServerConfiguration;
import emma.network.SendableClient;

public class Info implements Request {
    private final ServerConfiguration config;
    private final SendableClient client;

    public Info(SendableClient client, ServerConfiguration configuration) {
        this.config = configuration;
        this.client = client;
    }

    @Override
    public void write() {
        String data = config.info();
        client.send(Request.lengthOf(data));
        client.send(Request.newLine());
        client.send(data);
        client.send(Request.newLine());
        client.flush();
    }
}
