package com.dragon.talon.netty.tcp.code;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServer {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            final ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ByteBuf buf = Unpooled.copiedBuffer("$_".getBytes());
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, buf))
                                    .addLast(new StringDecoder()).addLast(new EchoServer.EchoServerHandler());
                            ;
                        }
                    });
            final ChannelFuture sync = b.bind(8081).sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    private static class EchoServerHandler extends ChannelInboundHandlerAdapter {
        int counter = 0;

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            String body = (String)msg;
            System.out.println("this is "+ ++counter+" time receive client [" +body);
            //分隔符号
            body += "$_";
            final ByteBuf buf = Unpooled.copiedBuffer(body.getBytes());
            ctx.writeAndFlush(buf);
        }
    }
}

