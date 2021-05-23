package emma.network;

import java.io.InputStream;
import java.io.OutputStream;

public interface ConnectionListener {
    void onConnected(InputStream in, OutputStream out);

    void onClose();

    void onError(Exception exception);

    void onMessage(byte[] data);

    void onMessage(String data);
}
