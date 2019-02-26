package com.dragon.talon.netty.tcp.code;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 使用FixedLengthFrameDecoder 解决粘包问题
 * 
 * 指定固定长的数据传输字符 根据固定的长度区分
 */
public class FiexEchoServer {
    public static void main(String[] args) {
        EventLoopGroup boot = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(boot,work).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new FixedLengthFrameDecoder(20))
                                    .addLast(new StringDecoder()).addLast(new FiexEchoServerHandler())
                                    ;
                        }
                    });
            final ChannelFuture future = b.bind(8082).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            boot.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
    
    private static class FiexEchoServerHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("Receive client : ["+msg+"]");
        }
    }
}
