package emma.io.websocket.protocol;

import emma.config.ServerConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AudioTest {
    private final HeaderTest.StringOutput out = new HeaderTest.StringOutput();
    private final Audio data = new Audio(new FakeClient(out), new ServerConfiguration(), new short[]{1});

    @Test void requestAudio() throws IOException {
        String expected = "b7\r\n" +
                ServerConfiguration.boundary() +
                "Content-Disposition: form-data; name=ConcludingAudioParameter; paramName=AUDIO_INFO\r\n" +
                "Content-Type: audio/x-wav;codec=pcm;bit=16;rate=16000\r\n" +
                "\r\n" +
                "\u0001\00\r\n";
        data.write();
        assertEquals(expected, out.getResult());
    }

}