package emma;



import emma.io.websocket.CerenceClient;
import emma.wav.WavFile;
import emma.wav.WavFileException;

import java.io.File;
import java.io.IOException;

public class App {
    private static final String AUDIO1 = "/home/alvaro/Music/fast2.wav";
    private static final int NUM_FRAMES_TO_READ = 640;
    public static long startTime = 0L;

    public static void main(String[] args) {


        System.setProperty("jsse.enableCBCProtection", "false");
        try {
            CerenceClient client = new CerenceClient();
            client.sendRequest();

            WavFile wavFile = WavFile.openWavFile(new File(AUDIO1));
            readAudio(client, wavFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void readAudio(CerenceClient client, WavFile wav) throws IOException, WavFileException {
        startTime = System.currentTimeMillis();
        int numChannels = wav.getNumChannels();
        short[] buffer = new short[NUM_FRAMES_TO_READ * numChannels];
        int framesRead;

        do {
            // Read frames into buffer
            framesRead = wav.readFrames(buffer, NUM_FRAMES_TO_READ);
            client.sendAudio(buffer);


        } while (framesRead != 0);

    }


}
