package de.semvox.research.asr;

import de.semvox.research.asr.netty.NettyServer;
import de.semvox.research.asr.tcp.TcpServer;
import de.semvox.research.asr.utils.Tuple2;
import de.semvox.research.asr.ws.AsrEndpoint;
import emma.config.ServerConfiguration;
import emma.network.ConnectionListener;
import emma.network.SendableClient;
import emma.network.SslClient;

import java.io.IOException;
import java.util.Objects;

public class App {
    public static void main(String[] args) throws InterruptedException {
        //System.setProperty("jsse.enableCBCProtection", "false");
        //startWs();
        //startTcp();
        startNetty();

    }

    private static void startNetty() throws InterruptedException {
        Objects.requireNonNull(NettyServer.openServerPort(() -> App::listenerFrom, 2701)).channel().closeFuture().sync();
    }

    private static void startTcp() {
        try {
            TcpServer server = new TcpServer(() -> App::listenerFrom, 2701);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startWs() throws InterruptedException {
        AsrEndpoint server = new AsrEndpoint(() ->App::listenerFrom, 2701);
        server.start();
        while (true) {
            Thread.sleep(100);
        }
    }

    private static Tuple2<SendableClient, ServerConfiguration> listenerFrom(ConnectionListener listener) {
        ServerConfiguration configuration = ServerConfiguration.from(App.class.getClassLoader().getResourceAsStream("asr_semvox.json"));
        return Tuple2.of(
                SslClient.newInstance(listener, configuration, false).orElseThrow(() -> new RuntimeException("Cannot get ssl conenection")),
                configuration
        );

    }
}
