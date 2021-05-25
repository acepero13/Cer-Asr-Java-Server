package de.semvox.research.asr.ws;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.semvox.research.asr.utils.Tuple2;
import de.semvox.research.asr.ws.cerence.SenderContext;
import emma.config.ServerConfiguration;
import emma.network.ConnectionListener;
import emma.network.SendableClient;
import emma.network.parser.ResponseParser;
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
    private static final HashMap<WebSocket, String> audioSender = new HashMap<>();
    private static final HashMap<String, WebSocket> recognitionReceiver = new HashMap<>();

    private final Supplier<Function<ConnectionListener, Tuple2<SendableClient, ServerConfiguration>>> factory;


    public AsrEndpoint(Supplier<Function<ConnectionListener, Tuple2<SendableClient, ServerConfiguration>>> factory, int port) {
        super(new InetSocketAddress(port));
        this.factory = factory;
    }

    private Optional<SessionListener> getListenerFrom(WebSocket ws) {
        return Optional.ofNullable(clients.get(ws));
    }


    private void respond(WebSocket ws, String data) {

        getListenerFrom(ws).ifPresent(l -> {
            new Thread(() -> respondToClient(ws, data)).start();
        });
    }

    private void respondToClient(WebSocket ws, String data) {
        String id = audioSender.getOrDefault(ws, "");
        Optional.ofNullable(recognitionReceiver.get(id)).ifPresent(w -> w.send(ResponseParser.parse(data)));
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
        if (message.contains("asr_event")) {
            final Optional<SessionListener> opListener = getListenerFrom(webSocket);
            String response = "{\"recognition_finished\": \"1\"}";
            opListener.ifPresent(l -> new Thread(() -> l.getClient().send(response)).start());
            respondToClient(webSocket, response);

        }
        else if (message.trim().startsWith("{") && message.trim().endsWith("}")) {
            JsonObject msg = JsonParser.parseString(message).getAsJsonObject();
            String clientId = msg.get("id").getAsString();
            if (msg.has("type") && msg.get("type").getAsString().equals("sender")) {
                audioSender.put(webSocket, clientId);
            } else if (msg.has("type") && msg.get("type").getAsString().equals("receiver")) {
                recognitionReceiver.put(clientId, webSocket);// Writing back
            }

        } else {
            final Optional<SessionListener> opListener = getListenerFrom(webSocket);
            opListener.ifPresent(l -> new Thread(() -> l.getClient().send(message)).start());
        }

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
            Logger.getLogger(TAG).info("data received for" + this + ": " + data);
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
