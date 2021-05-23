package emma.io.websocket.protocol;

import emma.config.ServerConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataTest {
    private final HeaderTest.StringOutput out = new HeaderTest.StringOutput();
    private final Data data = new Data(new FakeClient(out), new ServerConfiguration());

    @Test void requestData() throws IOException {
        String expected = "350\r\n" +
                ServerConfiguration.boundary() +
                "Content-Disposition: form-data; name=RequestData\r\n" +
                "Content-Type: application/json; charset=utf-8\r\n" +
                "\r\n" +
                "{\"appId\":\"SEMVOX1_CLOUD_ASR\",\"appKey\":\"81c439a64fbd75053aef33a0bf5393496692d58ab610d606e5a3dfb367a1fbe865358fb5f204ba2cf3edccb24160fba7f2d131a9e107afc7ab8e7a122f75fb94\",\"cmdDict\":{\"application\":\"BANMA\",\"application_name\":\"Cerence Cloud Client Application\",\"application_session_id\":\"sample_application_sessionid\",\"audio_source\":\"SpeakerAndMicrophone\",\"client_os_type\":\"Mac Sierra\",\"client_os_version\":\"10.0\",\"dictation_language\":\"deu-DEU\",\"dictation_type\":\"Automotive-Dictation\",\"locale\":\"Canada\",\"location\":\"\\u003c+45.5086699, -73.5539925\\u003e +/- 99.00m\",\"network_type\":\"4G\",\"organization_id\":\"Cerence\",\"ui_langugage\":\"de\"},\"cmdName\":\"NVC_ASR_CMD\",\"inCodec\":\"PCM_16_16K\",\"outCodec\":\"PCM_16_16K\",\"uId\":\"uid1\"}\r\n";
        data.write();
        assertEquals(expected, out.getResult());
    }
}