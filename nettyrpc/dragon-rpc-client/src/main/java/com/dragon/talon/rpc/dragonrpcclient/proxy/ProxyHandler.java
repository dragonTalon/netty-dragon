package com.dragon.talon.rpc.dragonrpcclient.proxy;

import com.dragon.talon.rpc.dragonrpcclient.netty.TcpClient;
import com.dragon.talon.rpc.dragonrpccommon.serializer.SerializerUtils;
import com.dragon.talon.rpc.dragonrpccommon.transfor.Request;
import com.dragon.talon.rpc.dragonrpccommon.transfor.Response;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelFuture;
import org.springframework.cglib.proxy.InvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Objects;

/**
 * 动态代理
 *
 * @author dragonboy
 */
public class ProxyHandler implements InvocationHandler {

    private String address;

    private int port;
    
    private static final Map<Class<?>, TcpClient> channelMap = Maps.newConcurrentMap();

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        Request request = Request.newInstance(method, o.getClass().getName(), objects);
        Class<?> declaringClass = method.getDeclaringClass();
        TcpClient tcpClient = channelMap.get(declaringClass);
        if (Objects.isNull(tcpClient)) {
            tcpClient = new TcpClient(address, port);
        }
        tcpClient.sendMsg(request);
        Response response = tcpClient.getResponse(request.getRequestId(), 10000);
        Class<?> returnType = method.getReturnType();
        Object obj = response.getObject();
        return getResult(obj, returnType);
    }

    public Object bind(Class<?>[] interfaces, String address, int port) {
        this.address = address;
        this.port = port;
        return Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), interfaces, this::invoke);
    }

    private <T> T getResult(Object obj, Class<T> clazz) {
        return (T) obj;
    }
}

