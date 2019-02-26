package com.dragon.talon.netty.codec.msgpack;

import com.dragon.talon.netty.codec.JavaSerializebleLow;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.util.List;

public class MsgServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup boot = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boot, work).channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1025)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                //解码器
                                .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,2,0,2))
                                .addLast(new MsgDecoder())
                                //半包编码
                                .addLast(new LengthFieldPrepender(2))
                                .addLast(new MsgEncoder())
                                .addLast(new MsgServerHandler());
                    }
                });

         ChannelFuture sync = bootstrap.bind(8080).sync();
        sync.channel().closeFuture().sync();
    }
    
    private static class MsgServerHandler extends ChannelInboundHandlerAdapter{
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println(msg);
            ctx.writeAndFlush("success");
            
        }
        
    }
}
