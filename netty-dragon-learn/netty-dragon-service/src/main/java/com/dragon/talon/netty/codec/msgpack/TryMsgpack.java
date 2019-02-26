package com.dragon.talon.netty.codec.msgpack;

import org.msgpack.MessagePack;
import org.msgpack.template.Templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TryMsgpack {
    /**
     * 编解码
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        List<String> src = new ArrayList<String>();
        src.add("talon");
        src.add("dragon");
        src.add("serializeble");

        MessagePack msgPack = new MessagePack();
        byte[] write = msgPack.write(src);

        List<String> read = (List<String>) msgPack.read(write, Templates.tList(Templates.tString()));
        System.out.println(read.get(0));
        System.out.println(read.get(1));
        System.out.println(read.get(2));
    }
}
