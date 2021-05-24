package de.semvox.research.asr.ws;

import de.semvox.research.asr.utils.Tuple2;
import emma.config.ServerConfiguration;
import emma.network.ConnectionListener;
import emma.network.SendableClient;
import emma.network.SslClient;
import emma.wav.WavFile;
import emma.wav.WavFileException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.function.Function;

class End2EndTest {
    private static final String AUDIO1 = "/home/alvaro/Music/fast2.wav";
    private static final int NUM_FRAMES_TO_READ = 640;
    private static long startTime;
    private AsrEndpoint server;
    private final HashMap<Integer, ConnectionListener> listenersMap = new HashMap<>();

    @BeforeEach
    void setUpServer() throws InterruptedException {
        ServerConfiguration config = ServerConfiguration.fromDefaultJson();
        server = new AsrEndpoint(this::createConnection, 2701);
        server.start();
        Thread.sleep(200);
    }

    private Function<ConnectionListener, Tuple2<SendableClient, ServerConfiguration>> createConnection() {
        return l -> {
            ServerConfiguration configuration = ServerConfiguration.fromDefaultJson();
            int id = listenersMap.size() + 1;
            SendableClient c = SslClient.newInstance(l, configuration, false).get();
            listenersMap.put(id, l);
            return Tuple2.of(c, configuration);
        };
    }

    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        server.stop();
    }

    @Test
    void testSingleClient() throws InterruptedException, IOException, WavFileException, URISyntaxException {

        WavFile wavFile = WavFile.openWavFile(new File(AUDIO1));
        AsrEndpointTest.TestClient cc = new AsrEndpointTest.TestClient(new URI("ws://127.0.0.1:2701/ws"));
        cc.connect();
        Thread.sleep(200);


        readAudio(cc, wavFile);
        Thread.sleep(40000);

    }

    @Test
    void testMultipleClients() throws InterruptedException {
        for (int i = 0; i < 10 ; i++) {
            new Thread(() -> {
                WavFile wavFile = null;
                try {
                    wavFile = WavFile.openWavFile(new File(AUDIO1));
                    AsrEndpointTest.TestClient cc = new AsrEndpointTest.TestClient(new URI("ws://127.0.0.1:2701/ws"));
                    cc.connect();
                    while (!cc.isOpen()) {
                        Thread.sleep(200);
                    }

                    readAudio(cc, wavFile);
                } catch (IOException | WavFileException | URISyntaxException | InterruptedException e) {
                    e.printStackTrace();
                }

            }).start();
        }
        Thread.sleep(40000);
    }



    private static void readAudio(AsrEndpointTest.TestClient client, WavFile wav) throws IOException, WavFileException {
        startTime = System.currentTimeMillis();
        int numChannels = wav.getNumChannels();
        short[] buffer = new short[NUM_FRAMES_TO_READ * numChannels];
        int framesRead;

        do {
            // Read frames into buffer
            framesRead = wav.readFrames(buffer, NUM_FRAMES_TO_READ);
            client.send(shorts2bytes(buffer));
        } while (framesRead != 0);

    }

    private static byte[] shorts2bytes(short[] data) {
        byte[] bytes2 = new byte[data.length * 2];
        ByteBuffer.wrap(bytes2).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(data);
        return bytes2;
    }
}
