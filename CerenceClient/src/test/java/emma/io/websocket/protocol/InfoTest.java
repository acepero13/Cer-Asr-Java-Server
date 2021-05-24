package emma.io.websocket.protocol;

import emma.config.ServerConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InfoTest {
    private final HeaderTest.StringOutput out = new HeaderTest.StringOutput();
    private final ServerConfiguration config = ServerConfiguration.fromDefaultJson();
    private final Info data = new Info(new FakeClient(out), config);

    @Test
    void requestData() throws IOException {
       String expected = "109\r\n" +
               config.boundary() +
               "Content-Disposition: form-data; name=DictParameter; paramName=REQUEST_INFO\r\n" +
               "Content-Type: application/json; charset=utf-8\r\n" +
               "\r\n" +
               "{\"start\":0,\"end\":0,\"text\":\"\",\"intermediate_response_mode\":\"UtteranceDetectionWithPartialRecognition\"}\r\n";
        data.write();
        assertEquals(expected, out.getResult());
    }
}