package emma.io.websocket.protocol;

import emma.config.ServerConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HeaderTest {
    private final StringOutput out = new StringOutput();
    private final ServerConfiguration config = ServerConfiguration.fromDefaultJson();
    private final Header header = new Header(new FakeClient(out),config);

    @Test
    void requestHeaders() throws IOException {

        String expected = "POST /NmspServlet/ HTTP/1.1\r\n" +
                "Transfer-Encoding: chunked\r\n" +
                "Connection: Keep-Alive\r\n" +
                "Content-Type: multipart/form-data; boundary=" +  config.rawBoundary() +"\r\n\r\n";
        header.write();
        assertEquals(expected, out.getResult());

    }

    public static class StringOutput extends OutputStream {
        private String result = "";

        public String getResult() {
            return result;
        }

        @Override
        public void write(int i) throws IOException {
            result += i;
        }

        public void write(byte[] bytes) throws IOException {
            result += new String(bytes, StandardCharsets.UTF_8);
        }
    }

}