package emma.network;

public interface SendableClient {
    void connect();

    void disconnect();

    void send(byte[] data);

    void send(short[] data);

    void send(String toSend);

    void flush();
}
