package de.semvox.research.asr.netty;

import de.semvox.research.asr.utils.Tuple2;
import de.semvox.research.asr.ws.cerence.SenderContext;
import emma.config.ServerConfiguration;
import emma.network.ConnectionListener;
import emma.network.SendableClient;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.function.Function;
import java.util.function.Supplier;

public class NettyHandler extends ChannelDuplexHandler implements ConnectionListener {
    private final SenderContext sender;
    private ChannelHandlerContext context;


    public NettyHandler(Supplier<Function<ConnectionListener, Tuple2<SendableClient, ServerConfiguration>>> factory) {
        Tuple2<SendableClient, ServerConfiguration> clientConf = factory.get().apply(this);
        this.sender = new SenderContext(clientConf.first(), clientConf.second());
        clientConf.first().connect();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        this.context = ctx;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        //ctx.writeAndFlush(byteBuf.array());
        byte[] buff =  new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(buff);
        sender.send(buff);

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

    }

    @Override
    public void onConnected(InputStream in, OutputStream out) {
        System.out.println("Connected with cerence");
    }

    @Override
    public void onClose() {

    }

    @Override
    public void onError(Exception exception) {
        System.out.println("Error with cerence " + exception.getMessage());

    }

    @Override
    public void onMessage(byte[] data) {

    }

    @Override
    public void onMessage(String data) {
        if (context != null) {
            //System.out.println("Received: " + data);
            String timestamp = System.currentTimeMillis() + " || " +  data + "\r\n";
            System.out.println(timestamp);

            context.writeAndFlush(Unpooled.copiedBuffer(timestamp, Charset.defaultCharset()));
            //context.writeAndFlush(data);
        }
    }
}
