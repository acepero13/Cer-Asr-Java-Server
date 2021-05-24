package de.semvox.research.asr.ws;

import de.semvox.research.asr.utils.Tuple2;
import de.semvox.research.asr.ws.utils.FakeClient;
import emma.config.ServerConfiguration;
import emma.network.ConnectionListener;
import emma.network.SendableClient;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Function;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AsrEndpointTest {

    @Test
    void connectOneClient() throws URISyntaxException, InterruptedException, IOException {
        AsrEndpoint server = new AsrEndpoint(this::createConnection, 2701);
        server.start();
        TestClient cc = new TestClient(new URI("ws://127.0.0.1:2701/ws"));
        cc.connect();
        Thread.sleep(100);
        assertEquals(1, server.getConnections().size());
        server.stop();
    }

    private Function<ConnectionListener, Tuple2<SendableClient, ServerConfiguration>> createConnection() {
        return l -> Tuple2.of(new FakeClient(l),ServerConfiguration.fromDefaultJson());
    }

    @Test
    void connectTenClients() throws URISyntaxException, InterruptedException, IOException {
        AsrEndpoint server = new AsrEndpoint(this::createConnection, 2701);
        server.start();
        for (int i = 0; i < 10; i++) {
            TestClient cc = new TestClient(new URI("ws://127.0.0.1:2701/ws"));
            cc.connect();
            Thread.sleep(30);

        }
        assertEquals(10, server.getConnections().size());
        server.stop();
    }

    private Tuple2<SendableClient, ServerConfiguration> createConnection(ConnectionListener l) {
        return Tuple2.of(new FakeClient(l), ServerConfiguration.fromDefaultJson());
    }

    static class TestClient extends WebSocketClient {
        private static final String TAG = TestClient.class.getSimpleName();

        public TestClient(URI uri) {
            super(uri);
        }

        @Override
        public void onOpen(ServerHandshake serverHandshake) {

        }

        @Override
        public void onMessage(String s) {
            Logger.getLogger(TAG).info("Data received");
        }

        @Override
        public void onClose(int i, String s, boolean b) {

        }

        @Override
        public void onError(Exception e) {

        }
    }
}