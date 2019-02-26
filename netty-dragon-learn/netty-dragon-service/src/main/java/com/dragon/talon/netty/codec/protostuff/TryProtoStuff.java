package com.dragon.talon.netty.codec.protostuff;

public class TryProtoStuff {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        StuffInfo info = new StuffInfo();
        info.setInfo("talon");
        info.setInfoID(18);
        final byte[] encoder = SrizelizableStuff.encoder(info);
        System.out.println(encoder.length);
        final StuffInfo decord = SrizelizableStuff.decord(encoder, StuffInfo.class);
        System.out.println(decord);
    }
}
