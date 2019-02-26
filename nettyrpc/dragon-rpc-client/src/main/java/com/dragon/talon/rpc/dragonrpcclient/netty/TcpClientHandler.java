package com.dragon.talon.rpc.dragonrpcclient.netty;

import com.dragon.talon.rpc.dragonrpccommon.transfor.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Optional;

/**
 * 请求处理
 * 
 * @author dragonboy 
 */
public class TcpClientHandler extends ChannelInboundHandlerAdapter {
    private TcpClient tcpClient;
    
    public TcpClientHandler(TcpClient tcpClient){
        this.tcpClient = tcpClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Response resp = (Response) msg;
        if (resp == null){
            throw new Exception("错误的请求来源 ，返回是空的");
        }
        tcpClient.recevier(resp);
    }
}
