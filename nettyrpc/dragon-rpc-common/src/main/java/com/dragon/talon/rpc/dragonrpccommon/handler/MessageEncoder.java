package com.dragon.talon.rpc.dragonrpccommon.handler;

import com.dragon.talon.rpc.dragonrpccommon.common.TypeEnum;
import com.dragon.talon.rpc.dragonrpccommon.serializer.SerializerUtils;
import com.dragon.talon.rpc.dragonrpccommon.transfor.Request;
import com.dragon.talon.rpc.dragonrpccommon.transfor.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 加密
 * @author dragonboy 
 */
public class MessageEncoder extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf buf) throws Exception {
        byte[] messageByte = null;
        if (msg instanceof Request){
            Request request = (Request)msg;
            messageByte = SerializerUtils.serialize(request);
            buf.writeInt(TypeEnum.request.getValue());
        }
        
        if (msg instanceof Response){
            Response resp = (Response)msg;
            messageByte = SerializerUtils.serialize(resp);
            buf.writeInt(TypeEnum.response.getValue());
        }
        buf.writeInt(messageByte.length);
        buf.writeBytes(messageByte);
        
        ctx.writeAndFlush(buf);
        buf.retain();
    }
}
