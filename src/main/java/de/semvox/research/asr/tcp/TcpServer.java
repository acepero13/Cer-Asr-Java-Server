package de.semvox.research.asr.tcp;

import de.semvox.research.asr.utils.Tuple2;
import de.semvox.research.asr.ws.cerence.SenderContext;
import emma.config.ServerConfiguration;
import emma.io.websocket.protocol.Request;
import emma.network.ConnectionListener;
import emma.network.SendableClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class TcpServer {
    private static final String TAG = TcpServer.class.getSimpleName();
    private final Supplier<Function<ConnectionListener, Tuple2<SendableClient, ServerConfiguration>>> factory;
    private final ServerSocket serverSocket;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    public TcpServer(Supplier<Function<ConnectionListener, Tuple2<SendableClient, ServerConfiguration>>> factory, int port) throws IOException {
        this.factory = factory;
        serverSocket = new ServerSocket(port);
    }

    public void start() {
        while (isRunning.get()) {
            try {
                Socket s = serverSocket.accept();
                ServerThread client = new ServerThread(s);
                Tuple2<SendableClient, ServerConfiguration> connection = factory.get().apply(client.listener);
                SenderContext context = new SenderContext(connection.first(), connection.second());
                connection.first().connect();
                client.setContext(context); // TODO: Avoid placed called (should not be important when to call the set context)
                client.start();
                Logger.getLogger(TAG).info("Client accepted");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ServerThread extends Thread {
        private final InputStream is;
        private final CerenceListener listener;
        private SenderContext client;

        public ServerThread(Socket s) throws IOException {
            is = s.getInputStream();
            this.listener = new CerenceListener(s.getOutputStream());
        }

        @Override
        public void run() {
            WriterWorker writer = new WriterWorker(getClient());
            Thread writerThread = new Thread(writer);
            ReaderWorker reader = new ReaderWorker(writer, is);
            Thread readerThread = new Thread(reader);

            readerThread.start();
            waitForConnection();
            writerThread.start(); // Only start sending after the cerence client is ready

            waitFor(writerThread, readerThread);
        }

        private void waitForConnection() {// TODO: As waitable
            while (!listener.isConnected.get()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        private void waitFor(Thread writerThread, Thread readerThread) {
            try {
                writerThread.join();
                readerThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
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
    }

    public static class CerenceListener implements ConnectionListener {
        private final Lock writerLock = new ReentrantLock();
        private final OutputStream os;
        private final AtomicBoolean isConnected = new AtomicBoolean(false);

        private CerenceListener(OutputStream os) {
            this.os = os;
        }

        @Override
        public void onConnected(InputStream in, OutputStream out) {
            isConnected.set(true);
        }

        @Override
        public void onClose() {
            isConnected.set(false);

        }

        @Override
        public void onError(Exception exception) {

        }

        @Override
        public void onMessage(byte[] data) {

        }

        @Override
        public void onMessage(String data) {
            writerLock.lock();
            new Thread(() -> {
                try {
                    Logger.getLogger(TAG).info("DATA received: " + data);
                    os.write(data.getBytes(StandardCharsets.UTF_8));
                    os.write(Request.newLine());
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            writerLock.unlock();
        }
    }
}
