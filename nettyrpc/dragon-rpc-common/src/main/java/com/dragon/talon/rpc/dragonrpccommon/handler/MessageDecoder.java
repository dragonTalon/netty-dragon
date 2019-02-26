package com.dragon.talon.rpc.dragonrpccommon.handler;

import com.dragon.talon.rpc.dragonrpccommon.common.TypeEnum;
import com.dragon.talon.rpc.dragonrpccommon.serializer.SerializerUtils;
import com.dragon.talon.rpc.dragonrpccommon.transfor.Request;
import com.dragon.talon.rpc.dragonrpccommon.transfor.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MessageDecoder extends ByteToMessageDecoder {

    private final static int HEAD = 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        /**
         * 数据格式不正确 
         * 请求头都没有做什么
         */
        if (buf.readableBytes() < HEAD) {
            return;
        }
        buf.markReaderIndex();
        int type = buf.readInt();
        int dataLength = buf.readInt();
        
        if (buf.readableBytes() < dataLength){
            buf.markReaderIndex();
            return;
        }
        
        byte[] bytes = new byte[dataLength];
        
        buf.readBytes(bytes);
        Object obj = null;
        
        if (type == TypeEnum.request.getValue()){
            obj = SerializerUtils.deserialize(bytes,Request.class);
        }
        
        if (type == TypeEnum.response.getValue()){
            obj = SerializerUtils.deserialize(bytes,Response.class);
        }
        
        out.add(obj);
    }
}
