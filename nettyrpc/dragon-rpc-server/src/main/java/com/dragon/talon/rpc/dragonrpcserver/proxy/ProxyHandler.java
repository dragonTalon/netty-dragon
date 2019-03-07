package com.dragon.talon.rpc.dragonrpcserver.proxy;

import com.dragon.talon.rpc.dragonrpccommon.annotation.rpcProvider;
import com.dragon.talon.rpc.dragonrpccommon.transfor.Request;
import com.dragon.talon.rpc.dragonrpccommon.transfor.Response;
import com.dragon.talon.rpc.dragonrpccommon.zookeeper.DragonZk;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProxyHandler extends ChannelInboundHandlerAdapter {

    private final static Map<String, Object> CurrentMap = Maps.newConcurrentMap();

    public ProxyHandler(Map<String, Object> application) {
        for (Map.Entry<String,Object> entry : application.entrySet()){
             Class<?>[] interfaces = entry.getValue().getClass().getInterfaces();
            Arrays.stream(interfaces).map(s->s.getName()).forEach(i->CurrentMap.put(i,entry.getValue()));
        }
        
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) {
            return;
        }
        Request request = (Request) msg;

        Object obj = CurrentMap.get(request.getClazz());
        if (Optional.ofNullable(obj).isPresent()) {
            Method declaredMethod = obj.getClass().getDeclaredMethod(request.getMethodName(), request
                    .getParamClass());
            Object invoke = declaredMethod.invoke(obj, request.getArg());

            Response resp = new Response();
            resp.setRequestId(request.getRequestId());
            resp.setSuccess(true);
            resp.setObject(invoke);
            if (invoke instanceof Throwable) {
                resp.setObject(null);
                resp.setThrowable((Throwable) invoke);
            }
            ctx.writeAndFlush(resp);
        }

    }
}
