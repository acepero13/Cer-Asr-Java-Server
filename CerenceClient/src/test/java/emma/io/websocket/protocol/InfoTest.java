package emma.io.websocket.protocol;

import emma.config.ServerConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InfoTest {
    private final HeaderTest.StringOutput out = new HeaderTest.StringOutput();
    private final Info data = new Info(new FakeClient(out), new ServerConfiguration());

    @Test
    void requestData() throws IOException {
       String expected = "109\r\n" +
               ServerConfiguration.boundary() +
               "Content-Disposition: form-data; name=DictParameter; paramName=REQUEST_INFO\r\n" +
               "Content-Type: application/json; charset=utf-8\r\n" +
               "\r\n" +
               "{\"end\":0,\"intermediate_response_mode\":\"UtteranceDetectionWithPartialRecognition\",\"start\":0,\"text\":\"\"}\r\n";
        data.write();
        assertEquals(expected, out.getResult());
    }
}