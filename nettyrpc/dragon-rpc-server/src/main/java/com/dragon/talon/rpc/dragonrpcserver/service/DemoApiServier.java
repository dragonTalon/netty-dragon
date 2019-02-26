package com.dragon.talon.rpc.dragonrpcserver.service;

import com.dragon.talon.rpc.dragonrpccommon.annotation.rpcProvider;
import com.dragon.talon.rpc.dragonrpccommon.api.DemoApi;
import org.springframework.stereotype.Service;

@Service
@rpcProvider
public class DemoApiServier implements DemoApi {
    
    public String demo(String arg) {
        System.out.println("成功进入 ："+arg);
        return "response";
    }
}
