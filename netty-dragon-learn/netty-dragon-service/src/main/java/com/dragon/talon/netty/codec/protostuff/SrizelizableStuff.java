package com.dragon.talon.netty.codec.protostuff;


import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;

/**
 * 使用protostuff 实现序列化
 */
public class SrizelizableStuff {

    public static byte[] encoder(Object msg) {
        RuntimeSchema schema = RuntimeSchema.createFrom(msg.getClass());
        return ProtostuffIOUtil.toByteArray(msg, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
    }

    public static  <T> T decord(byte[] bytes, Class<T> tClass) throws IllegalAccessException, InstantiationException {
        final T obj = tClass.newInstance();
        RuntimeSchema schema = RuntimeSchema.createFrom(tClass);

        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }

}
