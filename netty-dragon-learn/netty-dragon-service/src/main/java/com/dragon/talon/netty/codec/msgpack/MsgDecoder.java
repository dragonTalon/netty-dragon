package com.dragon.talon.netty.codec.msgpack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

public class MsgDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        final int length = msg.readableBytes();
        final byte[] bytes = new byte[length];
        msg.getBytes(msg.readerIndex(),bytes,0,length);
        final MessagePack pack = new MessagePack();
        out.add(pack.read(bytes));
    }
}
