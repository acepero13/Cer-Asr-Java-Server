package de.semvox.research.asr.tcp;

import de.semvox.research.asr.utils.Tuple2;
import emma.config.ServerConfiguration;
import emma.network.ConnectionListener;
import emma.network.SendableClient;
import emma.network.SslClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

class TcpServerTest {

    private TcpServer server;

    @BeforeEach
    void setUpServer() {
        try {
            server = new TcpServer(() -> this::listenerFrom, 2701);

            new Thread(() -> server.start()).start();
            Thread.sleep(500);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Tuple2<SendableClient, ServerConfiguration> listenerFrom(ConnectionListener listener) {
        ServerConfiguration configuration = ServerConfiguration.fromDefaultJson();
        return Tuple2.of(
                SslClient.newInstance(listener, configuration, false).orElseThrow(() -> new RuntimeException("Cannot get ssl conenection")),
                configuration
        );
    }

    @Test
    void disconnectFromServer() throws IOException, InterruptedException {
        Socket clientSocket = new Socket("localhost", 2701);

        Thread.sleep(500);
        OutputStream out = clientSocket.getOutputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        clientSocket.close();
        out.close();
        in.close();
        Thread.sleep(500);
    }
}