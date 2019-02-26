package com.dragon.talon.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup boot = new NioEventLoopGroup(1);
        EventLoopGroup work = new NioEventLoopGroup();

      try{
          ServerBootstrap b = new ServerBootstrap();
          b.group(boot,work)
                  .channel(NioServerSocketChannel.class)
                  .option(ChannelOption.SO_BACKLOG,100)
                  .childHandler(new ChannelInitializer<SocketChannel>() {
                      @Override
                      protected void initChannel(SocketChannel ch) throws Exception {
                          ch.pipeline()
                                  .addLast(new HttpRequestDecoder())
                                  .addLast(new HttpObjectAggregator(65536))
                                  .addLast(new ChunkedWriteHandler());
                      }
                  });
          final ChannelFuture localhost = b.bind("localhost", 8080).sync();
          localhost.channel().closeFuture().sync();
      }finally {
          boot.shutdownGracefully();
          work.shutdownGracefully();
      }

    }
}
