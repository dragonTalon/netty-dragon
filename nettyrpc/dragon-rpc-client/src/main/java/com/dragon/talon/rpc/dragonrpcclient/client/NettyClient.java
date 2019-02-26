package com.dragon.talon.rpc.dragonrpcclient.client;

import com.dragon.talon.rpc.dragonrpcclient.netty.TcpClient;
import com.dragon.talon.rpc.dragonrpcclient.netty.TcpClientHandler;
import com.dragon.talon.rpc.dragonrpccommon.handler.MessageDecoder;
import com.dragon.talon.rpc.dragonrpccommon.handler.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 客户端启动
 *
 * @author dragonboy
 */
public class NettyClient {
    private NioEventLoopGroup work;
    
    private static ChannelFuture future;

    public void init(String address, int port, TcpClient tcpClient) {
        work = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(work)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4))
                                    .addLast(new LengthFieldPrepender(4))
                                    .addLast(new MessageDecoder())
                                    .addLast(new MessageEncoder())
                                    .addLast(new LoggingHandler())
                                    .addLast(new TcpClientHandler(tcpClient));
                        }
                    });

            future = bootstrap.connect(address, port).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (future != null) {
                future.channel().closeFuture().sync();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (work != null) {
                work.shutdownGracefully();
            }
        }

    }

    public ChannelFuture getFuture() {
        return future;
    }
}
