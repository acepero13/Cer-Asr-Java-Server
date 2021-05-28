package de.semvox.research.asr.netty;

import de.semvox.research.asr.utils.Tuple2;
import emma.config.ServerConfiguration;
import emma.network.ConnectionListener;
import emma.network.SendableClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;

import java.util.function.Function;
import java.util.function.Supplier;

public class NettyServer {
    private final Supplier<Function<ConnectionListener, Tuple2<SendableClient, ServerConfiguration>>> factory;
    int port;

    @Getter
    Channel channel;

    @Getter
    ChannelFuture channelFuture;

    EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    public NettyServer(Supplier<Function<ConnectionListener, Tuple2<SendableClient, ServerConfiguration>>> factory, int port) {
        this.port = port;
        this.factory = factory;
    }

    public static ChannelFuture openServerPort(Supplier<Function<ConnectionListener, Tuple2<SendableClient, ServerConfiguration>>> factory, int port) {
        try {
            NettyServer nettyServer = new NettyServer(factory, port);
            nettyServer.connectLoop();
            return nettyServer.getChannelFuture();
        } catch (Exception ignored) {
        }
        return null;
    }

    public void connectLoop() {
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new NettyHandler(factory));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)


            this.channel = f.channel();
            this.channelFuture = f;
        } catch (Exception e) {
            System.out.println("Exception in server thread" + e.getMessage());
        } finally {

        }
    }

    public void shutdown() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
