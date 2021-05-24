package emma.io.websocket;



import emma.App;
import emma.config.ServerConfiguration;
import emma.io.websocket.protocol.*;
import emma.network.ConnectionListener;
import emma.network.SendableClient;
import emma.network.SslClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class CerenceClient implements ConnectionListener {
    private static final String TAG = CerenceClient.class.getSimpleName();
    private final List<Request> protocol = new ArrayList<>();
    private static final ServerConfiguration config = ServerConfiguration.fromDefaultJson();
    private final SendableClient client;

    public CerenceClient() throws Exception {
        this.client = SslClient.newInstance(this, config, false).orElseThrow(() -> new Exception("Cannot connect client"));
        client.connect();
    }


    public void sendRequest() {
        protocol.forEach(Request::write);
    }


    @Override
    public void onConnected(InputStream in, OutputStream out) {
        String msg = "Connected to: " + config.host()  + ":" + config.port();
        Logger.getLogger(TAG).info(msg);
        protocol.addAll(Arrays.asList(new Header(client, config), new Data(client, config), new Info(client, config)));
    }

    @Override
    public void onClose() {
        Logger.getLogger(TAG).info("Closing connection");
    }

    @Override
    public void onError(Exception exception) {
        Logger.getLogger(TAG).warning("Error occurred. Reason: " + exception.getMessage());
        // TODO: Close connection??
    }

    @Override
    public void onMessage(byte[] data) {
        String msg = "Message bytes: " + Arrays.toString(data);
        Logger.getLogger(TAG).info(msg);

    }

    @Override
    public void onMessage(String data) {
        String msg = "Message: "+ data;
        Logger.getLogger(TAG).info(msg);
        Logger.getLogger(TAG).info("elapsed: " + (System.currentTimeMillis() - App.startTime) + " ms");
    }

    public void sendAudio(short[] buffer) {
        new Audio(client, config, buffer).write();
    }
}
