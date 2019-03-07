package com.dragon.talon.rpc.dragonrpcserver.netty;

import com.dragon.talon.rpc.dragonrpccommon.annotation.rpcProvider;
import com.dragon.talon.rpc.dragonrpccommon.handler.MessageDecoder;
import com.dragon.talon.rpc.dragonrpccommon.handler.MessageEncoder;
import com.dragon.talon.rpc.dragonrpccommon.zookeeper.DragonZk;
import com.dragon.talon.rpc.dragonrpcserver.proxy.ProxyHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * 启动服务
 *
 * @author dragonboy
 */
@Configuration
public class NettyServer {
    private NioEventLoopGroup boot;
    private NioEventLoopGroup work;
    private ChannelFuture future;
    @Value("${dragon.server.port}")
    private int port;
    @Value("${dragon.server.address}")
    private String address;
    
    private DragonZk dragonZk;

    @Autowired
    private ApplicationContext application;
    
    @PostConstruct
    public void init() {
        boot = new NioEventLoopGroup(1);
        work = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            final Map<String, Object> annotation = application.getBeansWithAnnotation(rpcProvider.class);
            serverBootstrap.group(boot, work)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4))
                                    .addLast(new LengthFieldPrepender(4))
                                    .addLast(new MessageDecoder())
                                    .addLast(new MessageEncoder())
                                    .addLast(new LoggingHandler())
                                    .addLast(new ProxyHandler(annotation));
                        }
                    });
            System.out.println("开启 netty rpc 之旅～");
            future = serverBootstrap.bind(address,port).sync();
            registZk(annotation,address+":"+port);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void registZk(Map<String, Object> annotation,String loacl){
        DragonZk dragonZk = DragonZk.newInstance("127.0.0.1:2181", 5000);
        for (Map.Entry<String,Object> entry : annotation.entrySet()){
            Class<?>[] interfaces = entry.getValue().getClass().getInterfaces();
            Arrays.stream(interfaces).map(s->s.getName()).forEach(s->dragonZk.registZkForService(loacl,s));
        }
    }
    
    public void close() {
        if (Optional.ofNullable(future).isPresent()) {
            try {
                future.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (boot != null) {
                    boot.shutdownGracefully();
                }
                if (work != null) {
                    work.shutdownGracefully();
                }
            }

        }
    }

}
