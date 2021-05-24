package emma.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonLoaderTest {
    private final String JSON_SAMPLE = "{\n" +
            "  \"protocol\": \"http\",\n" +
            "  \"host\": \"10.71.21.81\",\n" +
            "  \"port\": 28933,\n" +
            "  \"path\": \"/NmspServlet/\",\n" +
            "  \"headers\" : [\n" +
            "    \"Content-Type: multipart/form-data; boundary=sk29ksksk82ksmsgdfg4rgs5llopsja82\",\n" +
            "    \"Connection: Keep-Alive\",\n" +
            "    \"Transfer-Encoding: chunked\"\n" +
            "  ],\n" +
            "  \"multi-parts\": [\n" +
            "    {\n" +
            "      \"type\": \"json\",\n" +
            "      \"parameters\": [\n" +
            "        \"Content-Disposition: form-data; name=RequestData\",\n" +
            "        \"Content-Type: application/json; charset=utf-8\"\n" +
            "      ],\n" +
            "      \"body\": {\n" +
            "        \"cmdName\": \"NVC_ASR_CMD\",\n" +
            "        \"appId\": \"\",\n" +
            "        \"appKey\": \"\",\n" +
            "        \"uId\": \"uid1\",\n" +
            "        \"inCodec\": \"PCM_16_16K\",\n" +
            "        \"outCodec\": \"PCM_16_16K\",\n" +
            "        \"cmdDict\": {\n" +
            "          \"dictation_type\": \"ccpoi_nav\",\n" +
            "          \"application\": \"BANMA\",\n" +
            "          \"dictation_language\": \"cmn-CHN\",\n" +
            "          \"locale\": \"Canada\",\n" +
            "          \"application_name\": \"Cerence Cloud Client Application\",\n" +
            "          \"organization_id\": \"Cerence\",\n" +
            "          \"client_os_type\": \"Mac Sierra\",\n" +
            "          \"client_os_version\": \"10.0\",\n" +
            "          \"network_type\": \"4G\",\n" +
            "          \"audio_source\": \"SpeakerAndMicrophone\",\n" +
            "          \"location\": \"<+45.5086699, -73.5539925> +/- 99.00m\",\n" +
            "          \"application_session_id\": \"sample_application_sessionid\",\n" +
            "          \"ui_langugage\": \"en\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"json\",\n" +
            "      \"parameters\": [\n" +
            "        \"Content-Disposition: form-data; name=DictParameter; paramName=REQUEST_INFO\",\n" +
            "        \"Content-Type: application/json; charset=utf-8\"\n" +
            "      ],\n" +
            "      \"body\": {\n" +
            "        \"start\": 0,\n" +
            "        \"end\": 0,\n" +
            "        \"text\": \"\",\n" +
            "        \"intermediate_response_mode\": \"UtteranceDetectionWithPartialRecognition\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"audio\",\n" +
            "      \"parameters\": [\n" +
            "        \"Content-Disposition: form-data; name=ConcludingAudioParameter; paramName=AUDIO_INFO\",\n" +
            "        \"Content-Type: audio/x-wav;codec=pcm;bit=16;rate=16000\"\n" +
            "      ],\n" +
            "      \"body\": \"audio/KongTiaoXuYaoGongZuoLe.wav\",\n" +
            "      \"stream_enable\": true,\n" +
            "      \"stream_size\": 640,\n" +
            "      \"stream_timing\": \"30ms\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    private JsonLoader loader ;
    @Test void loadJson(){
        loader = JsonLoader.from(JSON_SAMPLE);
        assertNotNull(loader.load());
    }

}