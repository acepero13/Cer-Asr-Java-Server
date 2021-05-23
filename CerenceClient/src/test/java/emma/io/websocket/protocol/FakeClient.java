package emma.io.websocket.protocol;


import emma.network.SendableClient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class FakeClient implements SendableClient {
    private final HeaderTest.StringOutput out;

    public FakeClient(HeaderTest.StringOutput out) {
        this.out = out;
    }

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void send(byte[] data) {
        try {
            out.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(short[] data) {
        byte[] bytes2 = new byte[data.length * 2];
        ByteBuffer.wrap(bytes2).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(data);
        send(bytes2);
    }

    @Override
    public void send(String toSend) {
        try {
            out.write(toSend.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void flush() {
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
