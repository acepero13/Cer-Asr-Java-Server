package emma.network;



import emma.config.ServerConfiguration;
import emma.io.websocket.protocol.Request;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SslClient implements SendableClient {
    private final ConnectionListener listener;
    private final SSLSocket socket;
    private final OutputStream out;
    private final InputStream in;
    private final Thread receiverThread;
    private final Thread writerThread;
    private final ReaderThread reader;
    private final WriterThread writer;

    private SslClient(ConnectionListener listener, SocketFactory factory, boolean autoFlush) throws IOException {
        this.listener = listener;
        this.socket = factory.socket();
        this.out = factory.output();
        this.in = factory.input();
        this.reader = new ReaderThread(in, listener);
        this.receiverThread = new Thread(reader);
        this.writer = new WriterThread(out, listener, autoFlush);
        this.writerThread = new Thread(writer);

    }

    public static Optional<SendableClient> newInstance(ConnectionListener listener, ServerConfiguration config, boolean autoFlush) {
        try {
            return Optional.of(new SslClient(listener, SocketFactory.defaultSocket(config.host(), config.port()), autoFlush));
        } catch (IOException e) {
            listener.onError(e);
            return Optional.empty();
        }
    }

    public static Optional<SslClient> newInstance(ConnectionListener listener, ServerConfiguration config, boolean autoFlush, SocketFactory factory) {
        try {
            return Optional.of(new SslClient(listener, factory, autoFlush));
        } catch (IOException e) {
            listener.onError(e);
            return Optional.empty();
        }
    }

    @Override
    public void connect() {
        try {
            socket.startHandshake();
            receiverThread.start();
            writerThread.start();
            listener.onConnected(in, out);
        } catch (IOException | IllegalThreadStateException e) {
            listener.onError(e);
        }
    }

    @Override
    public void disconnect() {
        try {
            in.close();
            out.close();
            socket.close();
            listener.onClose();
            writer.isRunning.set(false);
            reader.isRunning.set(false);
        } catch (IOException e) {
            listener.onError(e);
        }
    }

    @Override
    public void send(byte[] data) {
        writer.write(data);
    }

    @Override
    public void send(short[] data) {
        byte[] bytes2 = new byte[data.length * 2];
        ByteBuffer.wrap(bytes2).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(data);
        send(bytes2);
    }

    @Override
    public void send(String txtData) {
        send(Request.bytesOf(txtData));
    }

    @Override
    public void flush() {
        try {
            out.flush();
        } catch (IOException e) {
            listener.onError(e);
        }
    }

    private static class WriterThread implements Runnable {
        private final OutputStream out;
        private final BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
        private final AtomicBoolean isRunning = new AtomicBoolean(true);
        private final ConnectionListener listener;
        private final boolean autoFlush;

        WriterThread(OutputStream out, ConnectionListener listener, boolean autoFlush) {
            this.out = out;
            this.listener = listener;
            this.autoFlush = autoFlush;
        }

        private void write(byte[] data) {
            if(!isRunning.get()) {
                listener.onError(new Exception("Cannot write when consumer is closed"));
                return;
            }
            try {
                queue.put(data);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                listener.onError(e);
            }
        }


        @Override
        public void run() {
            while (isRunning.get()) {
                byte[] data;
                try {
                    data = queue.take();
                    if (!isRunning.get()) {
                        break;
                    }
                    println(data);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            }

        }

        private void println(byte[] data) {
            if (autoFlush) {
                writeAndFlush(data);
            } else {
                writeOut(data);
            }
        }

        private void writeOut(byte[] data) {
            try {
                out.write(data);
            } catch (IOException e) {
                listener.onError(e);
            }
        }

        private void writeAndFlush(byte[] data) {
            try {
                out.write(data);
                out.flush();
            } catch (IOException e) {
                listener.onError(e);
            }
        }
    }

    private static class ReaderThread implements Runnable {

        private final InputStream in;
        private final ConnectionListener listener;
        public final AtomicBoolean isRunning = new AtomicBoolean(true);

        private ReaderThread(InputStream in, ConnectionListener listener) {
            this.in = in;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                receive();
            } catch (IOException e) {
                listener.onError(e);
            }
        }

        private void receive() throws IOException {
            byte[] buff = new byte[1024];
            while (notEOF(buff) && isRunning.get()) {
                String result = new String(buff, StandardCharsets.UTF_8);
                listener.onMessage(result);
            }
            int a = 0;
        }

        private boolean notEOF(byte[] buff) throws IOException {
            return !String.valueOf(in.read(buff, 0, buff.length - 1)).equals("-1");
        }
    }

}
