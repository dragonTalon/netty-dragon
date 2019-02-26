package com.dragon.talon.rpc.dragonrpcclient.service;

import com.dragon.talon.rpc.dragonrpccommon.annotation.Consumer;
import com.dragon.talon.rpc.dragonrpccommon.api.DemoApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 测试类
 * 
 * @author dragonboy 
 */
@Service
public class DemoClient {
    @Consumer
    private DemoApi api;

    public DemoApi getApi() {
        return api;
    }

    public void setApi(DemoApi api) {
        this.api = api;
    }

    public void demo(){
        final String message = api.demo("send message");
        System.out.println("take in "+ message);
        
    }
}
