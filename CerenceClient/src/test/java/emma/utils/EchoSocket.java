package emma.utils;


import emma.network.SocketFactory;

import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EchoSocket implements SocketFactory {
    private final MyInputStream in = new MyInputStream();
    private final OutputStream out = new MyOutputStream(in);
    @Override
    public OutputStream output() throws IOException {
        return out;
    }

    @Override
    public InputStream input() throws IOException {
        return in;
    }

    @Override
    public SSLSocket socket() {
        return new FakeSocket();
    }

    private static class MyOutputStream extends OutputStream {
        private final MyInputStream in;

        private MyOutputStream(MyInputStream in) {
            this.in = in;
        }
        private final StringBuilder builder = new StringBuilder();

        @Override
        public void write(int i) throws IOException {
            builder.append(i);
        }

        @Override
        public void write(byte[] b) throws IOException {
            builder.append(Arrays.toString(b));
            try {
                in.queue.put(b);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class MyInputStream  extends InputStream{
        private BlockingQueue<byte[]> queue = new LinkedBlockingQueue<>();
        @Override
        public int read() throws IOException {
            while (true) {
                try {
                    queue.take();
                    return 1;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            while (true) {
                try {
                    byte[] received = queue.take();
                    System.arraycopy(received, 0, b, 0, received.length);
                    return 1;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class FakeSocket extends SSLSocket {
        @Override
        public String[] getSupportedCipherSuites() {
            return new String[0];
        }

        @Override
        public String[] getEnabledCipherSuites() {
            return new String[0];
        }

        @Override
        public void setEnabledCipherSuites(String[] strings) {

        }

        @Override
        public String[] getSupportedProtocols() {
            return new String[0];
        }

        @Override
        public String[] getEnabledProtocols() {
            return new String[0];
        }

        @Override
        public void setEnabledProtocols(String[] strings) {

        }

        @Override
        public SSLSession getSession() {
            return null;
        }

        @Override
        public void addHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListener) {

        }

        @Override
        public void removeHandshakeCompletedListener(HandshakeCompletedListener handshakeCompletedListener) {

        }

        @Override
        public void startHandshake() throws IOException {

        }

        @Override
        public void setUseClientMode(boolean b) {

        }

        @Override
        public boolean getUseClientMode() {
            return false;
        }

        @Override
        public void setNeedClientAuth(boolean b) {

        }

        @Override
        public boolean getNeedClientAuth() {
            return false;
        }

        @Override
        public void setWantClientAuth(boolean b) {

        }

        @Override
        public boolean getWantClientAuth() {
            return false;
        }

        @Override
        public void setEnableSessionCreation(boolean b) {

        }

        @Override
        public boolean getEnableSessionCreation() {
            return false;
        }
    }
}
