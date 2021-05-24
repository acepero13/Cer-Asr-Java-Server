package emma.io.websocket.protocol;


import emma.config.ServerConfiguration;
import emma.network.SendableClient;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Audio implements Request {
    private final ServerConfiguration config;
    private final SendableClient client;
    private final byte[] audioBytes;
    private final int multiplier;

    public Audio(SendableClient client, ServerConfiguration configuration, short[] audioShorts) {
        this.config = configuration;
        this.client = client;
        this.audioBytes = shorts2bytes(audioShorts);
        this.multiplier = 2;
    }

    public Audio(SendableClient client, ServerConfiguration configuration, byte[] audioBytes) {
        this.config = configuration;
        this.client = client;
        this.audioBytes = audioBytes;
        this.multiplier = 1;
    }

    @Override
    public void write() {
        String data = config.audio();

        int length = data.length() + audioBytes.length * multiplier;
        client.send(Request.lengthOf(length));
        client.send(Request.newLine());
        client.send(data);
        client.send(audioBytes);
        client.send(Request.newLine());
        client.flush();
    }

    private static byte[] shorts2bytes(short[] data) {
        byte[] bytes2 = new byte[data.length * 2];
        ByteBuffer.wrap(bytes2).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(data);
        return bytes2;
    }

}
