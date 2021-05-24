package emma.io.websocket.protocol;

import emma.config.ServerConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataTest {
    private final HeaderTest.StringOutput out = new HeaderTest.StringOutput();
    private final ServerConfiguration config = ServerConfiguration.fromDefaultJson();
    private final Data data = new Data(new FakeClient(out), config);

    @Test void requestData() throws IOException {
        String expected = "2aa\r\n" +
                config.boundary() +
                "Content-Disposition: form-data; name=RequestData\r\n" +
                "Content-Type: application/json; charset=utf-8\r\n" +
                "\r\n" +
                "{\"cmdName\":\"NVC_ASR_CMD\",\"appId\":\"\",\"appKey\":\"\",\"uId\":\"uid1\",\"inCodec\":\"PCM_16_16K\",\"outCodec\":\"PCM_16_16K\",\"cmdDict\":{\"dictation_type\":\"ccpoi_nav\",\"application\":\"BANMA\",\"dictation_language\":\"cmn-CHN\",\"locale\":\"Canada\",\"application_name\":\"Cerence Cloud Client Application\",\"organization_id\":\"Cerence\",\"client_os_type\":\"Mac Sierra\",\"client_os_version\":\"10.0\",\"network_type\":\"4G\",\"audio_source\":\"SpeakerAndMicrophone\",\"location\":\"<+45.5086699, -73.5539925> +/- 99.00m\",\"application_session_id\":\"sample_application_sessionid\",\"ui_langugage\":\"en\"}}\r\n";
        data.write();
        assertEquals(expected, out.getResult());
    }
}