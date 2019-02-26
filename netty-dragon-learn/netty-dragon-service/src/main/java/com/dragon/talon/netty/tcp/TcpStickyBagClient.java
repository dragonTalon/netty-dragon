package com.dragon.talon.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TcpStickyBagClient {
    public  static void main(String[] args) throws InterruptedException {
        final NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        try {
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new SolveStickyBagClientHandler());
            final ChannelFuture localhost = b.connect("localhost", 8080).sync();
            localhost.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
    static class ClientChannelHandler extends ChannelInboundHandlerAdapter{

        private byte[] req;

        private int counter;

        public ClientChannelHandler(){
            req = ("Query Time Order"+System.getProperty("line.separator")).getBytes();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ByteBuf buf = null;
            for (int i = 0 ;i<100 ;i++){
                buf = Unpooled.buffer(req.length);
                buf.writeBytes(req);
                ctx.writeAndFlush(buf);
            }
        }



        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            String body =(String)msg;
            System.out.println("now is "+body+" ; the counter is :" + ++counter);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }

    /**
     * 解决粘包 方式 
     * 加上 LineBasedFrameDecoder 
     */

    static class SolveStickyBagClientHandler extends  ChannelInitializer<SocketChannel>{
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline()
                    .addLast(new LineBasedFrameDecoder(1024))
                    .addLast(new StringDecoder())
                    .addLast(new ClientChannelHandler());
        }
    }
}

