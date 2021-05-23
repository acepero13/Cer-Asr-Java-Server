package emma.io.websocket.protocol;


import emma.config.ServerConfiguration;
import emma.network.SendableClient;

public class Header implements Request {
    private final ServerConfiguration config;
    private final String request;
    private final SendableClient client;

    public Header(SendableClient client, ServerConfiguration configuration) {
        this.config = configuration;
        this.client = client;
        this.request = String.join(" ", "POST", config.path(), "HTTP/1.1") + ServerConfiguration.NEW_LINE;
    }

    @Override
    public void write() {
        String toSend = request + config.headers();
        client.send(toSend);
        client.send(Request.newLine());
        client.flush();
    }


}
