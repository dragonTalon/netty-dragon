package com.dragon.talon.rpc.dragonrpcclient.netty;

import com.dragon.talon.rpc.dragonrpcclient.client.NettyClient;
import com.dragon.talon.rpc.dragonrpccommon.serializer.SerializerUtils;
import com.dragon.talon.rpc.dragonrpccommon.transfor.Request;
import com.dragon.talon.rpc.dragonrpccommon.transfor.Response;
import com.google.common.collect.Maps;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;


/**
 * 客户端
 *
 * @author dragonboy
 */
public class TcpClient {

    private Channel channel;
    
    private static final Map<String,ReceiverData> receiverDataMap = Maps.newConcurrentMap();
    
    public TcpClient(String host,int port){
        this.channel =  getChannel(host,port);
    }
    
    private Channel getChannel(String host, Integer port) {
        NettyClient nettyClient = new NettyClient();
        try {
            nettyClient.init(host, port,this);
            channel = nettyClient.getFuture().channel();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return channel;
    }
    
    public void sendMsg(Request request) throws Exception {
       if (channel != null){
           receiverDataMap.put(request.getRequestId(),new ReceiverData());
           channel.writeAndFlush(request);
       }else {
           System.out.println("消息发送失败，连接尚未建立");
       }
    }
    
    public Response getResponse(String requestId,long timeOut) throws Exception {
        ReceiverData receiverData = receiverDataMap.get(requestId);
        if (Objects.isNull(receiverData)){
            throw new Exception("get data wait no revice data ! request ID : "+requestId);
        }

        Response response = receiverData.getResponse(timeOut);
        if (Optional.ofNullable(response.getThrowable()).isPresent()){
            throw new Exception(response.getThrowable());
        }
        receiverDataMap.remove(requestId);
        return response;
    }
    
    protected void recevier(Response response){
        ReceiverData receiverData = receiverDataMap.get(response.getRequestId());
        if (Optional.ofNullable(receiverData).isPresent()){
            receiverData.setResponse(response);
            return;
        }
        System.out.println("是不是没有发请求啊，或者是请求丢失了？");
    }
}
