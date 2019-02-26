package com.dragon.talon.rpc.dragonrpccommon.serializer;

import com.google.common.collect.Maps;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.Optional;

/**
 * 序列化工具
 *
 * @author dragonboy
 */
public class SerializerUtils {

    private static Map<Class, RuntimeSchema> schemaMap = Maps.newConcurrentMap();

    /**
     * 编码
     *
     * @param obj 加密对象
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T obj) {
        RuntimeSchema runtimeSchema = schemaMap.get(obj.getClass());
        if (!Optional.ofNullable(runtimeSchema).isPresent()) {
            runtimeSchema = RuntimeSchema.createFrom(obj.getClass());
            schemaMap.put(obj.getClass(), runtimeSchema);
        }
        return ProtostuffIOUtil.toByteArray(obj, runtimeSchema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

    }

    /**
     * 解码
     *
     * @param message 解密字节
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T deserialize(byte[] message, Class<T> clazz) {
        RuntimeSchema runtimeSchema = schemaMap.get(clazz);
        if (!Optional.ofNullable(runtimeSchema).isPresent()) {
            runtimeSchema = RuntimeSchema.createFrom(clazz);
            schemaMap.put(clazz, runtimeSchema);
        }
        try {
            T obj = clazz.newInstance();
            ProtostuffIOUtil.mergeFrom(message, obj, runtimeSchema);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
