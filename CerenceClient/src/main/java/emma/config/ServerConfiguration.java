package emma.config;

import java.io.InputStream;
import java.util.Random;

public class ServerConfiguration {

    public static final String NEW_LINE = "\r\n";
    private static final int BOUNDARY_LENGTH = 33;


    private final String host;
    private final int port;
    private final String path;
    private final String headers;
    private final String data;
    private final String info;
    private final String audio;
    private final String boundary
            ;

    private ServerConfiguration(ConfigurationBuilder builder) {
        this.host = builder.host;
        this.port = builder.port;
        this.path = builder.path;
        this.headers = builder.headers;
        this.data = builder.data;
        this.info = builder.info;
        this.audio = builder.audio;
        this.boundary = builder.boundary;
    }

    public static ServerConfiguration fromDefaultJson() {
        return JsonLoader.from(ServerConfiguration.class.getClassLoader().getResourceAsStream("asr.json")).load();
    }

    public static ServerConfiguration from(InputStream is) {
        return JsonLoader.from(is).load();
    }

    private static String randomBoundaryGenerator(int targetStringLength) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }



    public static String endingBoundary() {
        return ""; // TODO: Finish this
    }

    public String boundary() {
        return NEW_LINE + "--" + boundary + NEW_LINE;
    }

    public String rawBoundary() {
        return boundary;
    }

    public String host() {
        return host;
    }

    public int port() {
        return port;
    }

    public String path() {
        return path;
    }


    public String data() {
        return boundary() + data;
    }

    public String info() {
        return boundary() + info;
    }

    public String audio() {
        return boundary() + audio + NEW_LINE;
    }

    public String headers() {
        return headers;
    }


    static class ConfigurationBuilder {
        private final String host;
        private final int port;
        private final String path;
        private final String boundary;
        private String headers;
        private String data;
        private String info;
        private String audio;

        ConfigurationBuilder(String host, int port, String path) {
            this.host = host;
            this.port = port;
            this.path = path;
            this.boundary = randomBoundaryGenerator(BOUNDARY_LENGTH);
        }


        public ConfigurationBuilder headers(String headers) {
            String boundaryKey = "boundary=";
            int boundaryIndex = headers.indexOf(boundaryKey);
            String staticBoundary = headers.substring(boundaryIndex, boundaryIndex + boundaryKey.length() + BOUNDARY_LENGTH);
            String newBoundary = boundaryKey + boundary;

            this.headers = headers.replace(staticBoundary, newBoundary);
            return this;
        }

        public ConfigurationBuilder data(String data) {
            this.data = data;
            return this;
        }

        public ConfigurationBuilder info(String info) {
            this.info = info;
            return this;
        }

        public ConfigurationBuilder audio(String audio) {
            this.audio = audio;
            return this;
        }

        public ServerConfiguration build() {
            return new ServerConfiguration(this);
        }
    }


}

