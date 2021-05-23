package emma.io.websocket.protocol;



import emma.config.ServerConfiguration;

import java.nio.charset.StandardCharsets;

public interface Request {
    static byte[] lengthOf(int length) {
        return bytesOf(Integer.toHexString(length));
    }

    static byte[] newLine() {
        return bytesOf(ServerConfiguration.NEW_LINE);
    }

    void write() ;

    static byte[] bytesOf(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    static byte[] lengthOf(String data) {
        return bytesOf(Integer.toHexString(data.length()));
    }
}
