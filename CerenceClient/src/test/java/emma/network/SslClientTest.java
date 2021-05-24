package emma.network;


import emma.config.ServerConfiguration;
import emma.utils.EchoSocket;
import emma.utils.StringInputStream;
import emma.utils.StringOutputStream;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
// TODO: Test client logic here..

class SslClientTest implements ConnectionListener {
    private final SocketFactory factory = new DummyFactory();
    private final AtomicBoolean isConnected = new AtomicBoolean(false);
    private final AtomicBoolean errorOccurred = new AtomicBoolean(false);
    private String error = "";
    private String received = "";

    private final SslClient client = SslClient.newInstance(this, ServerConfiguration.fromDefaultJson(), true, factory).get();

    @Test
    void onConnected() throws InterruptedException {
        client.connect();
        while (!isConnected.get()) {
            Thread.sleep(50);
        }
        assertTrue(isConnected.get());
    }

    @Test
    void cannotSendOnClosedStream() throws InterruptedException {
        client.connect();
        client.send("test1");
        client.disconnect();
        client.send("test2");
        client.send("tes3");
        while (!errorOccurred.get()) {
            Thread.sleep(50);
        }
        assertTrue(true);
    }

    @Test
    void readsFromEchoStream() throws InterruptedException {
        SocketFactory fac = new EchoSocket();
        SslClient echoClient = SslClient.newInstance(this, ServerConfiguration.fromDefaultJson(), true, fac).get();
        echoClient.connect();
        echoClient.send("echo");
        while (received.equals("")) {
            Thread.sleep(100);
        }
        assertEquals("echo", received.trim());
    }

    @Override
    public void onConnected(InputStream in, OutputStream out) {

        isConnected.set(true);
    }

    @Override
    public void onClose() {

    }

    @Override
    public void onError(Exception exception) {
        errorOccurred.set(true);
        error = exception.getMessage();
    }

    @Override
    public void onMessage(byte[] data) {

    }

    @Override
    public void onMessage(String data) {
        received = data;
    }

    private static class DummyFactory implements SocketFactory {
        @Override
        public OutputStream output() throws IOException {
            return new StringOutputStream();
        }

        @Override
        public InputStream input() throws IOException {
            return new StringInputStream();
        }

        @Override
        public SSLSocket socket() {
            return new EchoSocket.FakeSocket();
        }


    }
}