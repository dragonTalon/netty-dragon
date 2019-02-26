package com.dragon.talon.netty.tcp;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.util.Date;

/**
 * 发送消息特别少
 * 
 * 在频发发送的时候，100数据合并两条发送 粘包
 */
public class TcpStickyBagServer {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup bootGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bootGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new SolveStickyBagHandler());
                   /* .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TcpStickyBagHandler());
                        }
                    });*/
            final ChannelFuture sync = bootstrap.bind(8080).sync();
            sync.channel().closeFuture().sync();

        }catch (Exception e){
          e.printStackTrace();  
        } finally {
            bootGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    /**
     * 会发生粘包
     */
    static class TcpStickyBagHandler extends ChannelInboundHandlerAdapter {


        private int count;
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            String body = new String(bytes,"utf-8");
            System.out.println("time server receive order :"+body+ " count : "+ ++ count);
            ByteBuf byteBuf = Unpooled.copiedBuffer("ime server receive order\n".getBytes());
            ctx.writeAndFlush(byteBuf);


        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
        }
    }

    /**
     * 解决粘包 方式 
     * 加上 LineBasedFrameDecoder 
     */

    static class SolveStickyBagHandler extends  ChannelInitializer<SocketChannel>{
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new LineBasedFrameDecoder(1024))
                    .addLast(new TcpStickyBagHandler());
        }
    }
}

