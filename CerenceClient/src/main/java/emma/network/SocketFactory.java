package emma.network;

import sun.rmi.log.LogOutputStream;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SocketFactory {
    OutputStream output() throws IOException;

    InputStream input() throws IOException;

    SSLSocket socket();

    static SocketFactory defaultSocket(String host, int port) throws IOException {
        return new SocketFactory() {
            private final SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(host, port);

            @Override
            public OutputStream output() throws IOException {
                return new LoggerOutputStream (socket.getOutputStream());
            }

            @Override
            public InputStream input() throws IOException {
                return socket.getInputStream();
            }

            @Override
            public SSLSocket socket() {
                return socket;
            }
        };
    }
}
