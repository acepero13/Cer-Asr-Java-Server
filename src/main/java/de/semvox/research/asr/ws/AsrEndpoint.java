package de.semvox.research.asr.ws;

import de.semvox.research.asr.utils.Tuple2;
import de.semvox.research.asr.ws.cerence.SenderContext;
import emma.config.ServerConfiguration;
import emma.network.ConnectionListener;
import emma.network.SendableClient;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;


public class AsrEndpoint extends WebSocketServer {
    private static final String TAG = AsrEndpoint.class.getSimpleName();
    private static final HashMap<WebSocket, SessionListener> clients = new HashMap<>();
    private final Supplier<Function<ConnectionListener, Tuple2<SendableClient, ServerConfiguration>>> factory;



    public AsrEndpoint(Supplier<Function<ConnectionListener, Tuple2<SendableClient, ServerConfiguration>>> factory, int port) {
        super(new InetSocketAddress(port));
        this.factory = factory;
    }

    private Optional<SessionListener> getListenerFrom(WebSocket ws) {
        return Optional.ofNullable(clients.get(ws));
    }


    private void respond(WebSocket ws, String data) {
        getListenerFrom(ws).ifPresent(l -> new Thread(() -> l.ws.send(data)).start());
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        SessionListener listener = new SessionListener(webSocket, this);
        Tuple2<SendableClient, ServerConfiguration> clientConfig = factory.get().apply(listener);

        SenderContext context = new SenderContext(clientConfig.first(), clientConfig.second());
        listener.setContext(context);
        clients.put(webSocket, listener);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        Logger.getLogger(TAG).warning("Closing session: " + webSocket.toString());
    }

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        final Optional<SessionListener> opListener = getListenerFrom(webSocket);
        opListener.ifPresent(l -> new Thread(() -> l.getClient().send(message)).start());
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteBuffer message) {
        final Optional<SessionListener> opListener = getListenerFrom(webSocket);
        opListener.ifPresent(l -> new Thread(() -> l.getClient().send(message.array())).start());
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        Logger.getLogger(TAG).warning("Error occurred in ws: " + e.getMessage());
    }

    @Override
    public void onStart() {
        Logger.getLogger(TAG).info("Server started on port: " + getPort());
    }

    private static class SessionListener implements ConnectionListener {
        private final WebSocket ws;
        private final AsrEndpoint endpoint;
        private SenderContext client;
        private final String id = UUID.randomUUID().toString();

        public SessionListener(WebSocket ws, AsrEndpoint asrEndpoint) {
            this.ws = ws;
            this.endpoint = asrEndpoint;
        }

        @Override
        public void onConnected(InputStream in, OutputStream out) {
            Logger.getLogger(TAG).info("Connected with Cerence server");
        }

        @Override
        public void onClose() {
            Logger.getLogger(TAG).info("Closing connection with Cerence server");
        }

        @Override
        public void onError(Exception exception) {
            Logger.getLogger(TAG).info("Error receiving message from cerence. Cause: " + exception.getMessage());
        }

        @Override
        public void onMessage(byte[] data) {
            Logger.getLogger(TAG).info("Byte response??");
        }

        @Override
        public void onMessage(String data) {
            Logger.getLogger(TAG).info("data received for" + this + ": " + data  );
            endpoint.respond(ws, data);
        }

        public void setContext(SenderContext client) {
            this.client = client;
        }

        public SenderContext getClient() {
            if (client == null) {
                Logger.getLogger(TAG).warning("Client is null!!!");
                throw new NullPointerException("Client cannot be null"); // should not happen
            }
            return client;
        }

        public String toString() {
            return "SessionListener(" + id + ")";
        }
    }


}
