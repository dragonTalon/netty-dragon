package com.dragon.talon.netty.tcp.code;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 使用 DelimiterBasedFrameDecoder 类解决tcp的粘包问题，
 *      这个类通过服务器与客户端之间项目约定 分隔方式来分隔消息
 */
public class EchoClient {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            final Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //指定分隔符号
                            ByteBuf buf = Unpooled.copiedBuffer("$_".getBytes());
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,buf))
                                    .addLast(new StringDecoder()).addLast(new EchoClientHandler())
                                    ;
                        }
                    });
            final ChannelFuture f = b.connect("localhost", 8081).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
    
  
    
    static final String message = "hi , welcone to netty.$_";
    
    private static class EchoClientHandler extends ChannelInboundHandlerAdapter{
        private  int count = 0;
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            for (int i = 0;i<10;i++){
                ctx.writeAndFlush(Unpooled.copiedBuffer(message.getBytes()));
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("this is "+ ++count + "   times : " + (String) msg);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }
    } 
}
