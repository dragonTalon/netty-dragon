package com.dragon.talon.netty.codec.msgpack;

import com.dragon.talon.netty.codec.JavaSerializebleLow;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.util.ArrayList;
import java.util.List;

public class MsgClient {
    private static String host = "localhost";

    private static int port = 8080;

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        try {

            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,2,0,2))
                                    .addLast("msgpack decoder", new MsgDecoder())
                                    .addLast(new LengthFieldPrepender(2))
                                    .addLast("msgpac encode", new MsgEncoder())
                                    .addLast(new MsgClientHandler());
                        }
                    });
            final ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }

    }
    
    private static class MsgClientHandler extends ChannelInboundHandlerAdapter{
        private List<UserInfo > userInfos(){
            List<UserInfo > userInfoList = new ArrayList<UserInfo>();
            final UserInfo userInfo = new UserInfo();
            userInfo.build("talon",18);
            userInfoList.add(userInfo);
            return userInfoList;
        }
        
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            final List<UserInfo> userInfoList = userInfos();
            int i = 0;
            while (i <100){
                i++;
                for (UserInfo userInfo : userInfoList){
                    ctx.writeAndFlush(userInfo);
                }
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println(msg);
        }
    }
}
