package com.dragon.talon.rpc.dragonrpcclient.netty;

import com.dragon.talon.rpc.dragonrpccommon.transfor.Response;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 阻塞请求
 * 
 * @author dragonboy 
 */
public class ReceiverData {
    private Response response;
    
    private CountDownLatch countDownLatch;
    
    public ReceiverData(){
        countDownLatch = new CountDownLatch(1);
    }
    
    public Response getResponse(long waitTime) throws InterruptedException {
        countDownLatch.await(waitTime,TimeUnit.MILLISECONDS);
        return response;
    }
    
    public void setResponse(Response response){
        this.response = response;
        countDownLatch.countDown();
    }
    
}
