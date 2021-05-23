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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class TcpServer {
    private static final String TAG = TcpServer.class.getSimpleName();
    private final Supplier<Function<ConnectionListener, Tuple2<SendableClient, ServerConfiguration>>> factory;
    private final ServerSocket serverSocket;
    public static long startTime = 0L;

    public TcpServer(Supplier<Function<ConnectionListener, Tuple2<SendableClient, ServerConfiguration>>> factory, int port) throws IOException {
        this.factory = factory;
        serverSocket = new ServerSocket(port);
    }

    public void start() {
        while (true) {
            try {
                Socket s = serverSocket.accept();
                ServerThread client = new ServerThread(s);
                Tuple2<SendableClient, ServerConfiguration> connection = factory.get().apply(client);
                SenderContext context = new SenderContext(connection.first(), connection.second());
                connection.first().connect();
                client.setContext(context);
                client.start();
                Logger.getLogger(TAG).info("Client accepted");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ServerThread extends Thread implements ConnectionListener {
        private final Socket socket;
        private final Thread thread;
        private OutputStream os;
        private InputStream is;
        private SenderContext client;
        private final WriterThread writerWorker = new WriterThread();
        private final Lock writerLock = new ReentrantLock();

        public ServerThread(Socket s) {
            this.socket = s;
            this.thread = new Thread(writerWorker);
            thread.start();
        }

        public void run() {
            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();


                while (true) {
                    try {
                        byte[] buff = new byte[1280];
                        int read = is.read(buff);
                        writerWorker.write(buff);
                        if (startTime == 0) {
                            startTime = System.currentTimeMillis();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private class WriterThread implements Runnable {
            private final BlockingQueue<byte[]> queue;

            private WriterThread() {
                this.queue = new LinkedBlockingQueue<>();
            }

            @Override
            public void run() {
                while (true) {

                    try {
                        byte[] data = queue.take();
                        getClient().send(data);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }

            private void write(byte[] data) {
                queue.offer(data);
            }
        }

        @Override
        public void onConnected(InputStream in, OutputStream out) {

        }

        @Override
        public void onClose() {

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
                    Logger.getLogger(TAG).info("elapsed after receiving: " + (System.currentTimeMillis() - startTime));
                    os.write(data.getBytes(StandardCharsets.UTF_8));
                    os.write(Request.newLine());
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            writerLock.unlock();

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
}
