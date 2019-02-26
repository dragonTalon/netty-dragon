package com.dragon.talon.netty.codec.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

import java.io.IOException;

public class MsgEncoder extends MessageToByteEncoder {
    
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)  {
        try{
            MessagePack pack = new MessagePack();
            final byte[] write = pack.write(msg);
            out.writeBytes(write);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
