package emma.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServerConfigurationTest {
    private final ServerConfiguration config = ServerConfiguration.fromDefaultJson();
    @Test void requestsHeaders(){
        String expected = "Transfer-Encoding: chunked\r\n" +
                "Connection: Keep-Alive\r\n" +
                "Content-Type: multipart/form-data; boundary=" + config.rawBoundary() + "\r\n";

        assertEquals(expected, config.headers());

    }

    @Test void requestData(){
        String expected = config.boundary() +
                "Content-Disposition: form-data; name=RequestData\r\n" +
                "Content-Type: application/json; charset=utf-8\r\n" +
                "\r\n" +
                "{\"cmdName\":\"NVC_ASR_CMD\",\"appId\":\"\",\"appKey\":\"\",\"uId\":\"uid1\",\"inCodec\":\"PCM_16_16K\",\"outCodec\":\"PCM_16_16K\",\"cmdDict\":{\"dictation_type\":\"ccpoi_nav\",\"application\":\"BANMA\",\"dictation_language\":\"cmn-CHN\",\"locale\":\"Canada\",\"application_name\":\"Cerence Cloud Client Application\",\"organization_id\":\"Cerence\",\"client_os_type\":\"Mac Sierra\",\"client_os_version\":\"10.0\",\"network_type\":\"4G\",\"audio_source\":\"SpeakerAndMicrophone\",\"location\":\"<+45.5086699, -73.5539925> +/- 99.00m\",\"application_session_id\":\"sample_application_sessionid\",\"ui_langugage\":\"en\"}}";
        assertEquals(expected, config.data());
    }

    @Test void requestInfo(){
        String expected = config.boundary() +
                "Content-Disposition: form-data; name=DictParameter; paramName=REQUEST_INFO\r\n" +
                "Content-Type: application/json; charset=utf-8\r\n" +
                "\r\n" +
                "{\"start\":0,\"end\":0,\"text\":\"\",\"intermediate_response_mode\":\"UtteranceDetectionWithPartialRecognition\"}";

        assertEquals(expected, config.info());
    }

    @Test void requestAudio(){
        String expected = config.boundary() +
                "Content-Disposition: form-data; name=ConcludingAudioParameter; paramName=AUDIO_INFO\r\n" +
                "Content-Type: audio/x-wav;codec=pcm;bit=16;rate=16000\r\n\r\n";
        assertEquals(expected, config.audio());
    }

}