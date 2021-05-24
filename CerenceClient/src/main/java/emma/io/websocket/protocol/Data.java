package emma.io.websocket.protocol;


import emma.config.ServerConfiguration;
import emma.network.SendableClient;

public class Data implements Request {
    private final ServerConfiguration config;
    private final SendableClient client;

    public Data(SendableClient client, ServerConfiguration configuration) {
        this.config = configuration;
        this.client = client;
    }

    @Override
    public void write() {
        String data = config.data();
        client.send(Request.lengthOf(data));
        client.send(Request.newLine());
        client.send(data);
        client.send(Request.newLine());
        client.flush();

    }


}
