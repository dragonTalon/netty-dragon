package com.dragon.talon.rpc.dragonrpcclient.proxy;

/**
 * 代理工厂
 * 
 * @author dragonboy 
 */
public class ProxyFactory {
    public static Object create(Class<?>[] clazz,String address,int port){
        ProxyHandler handler = new ProxyHandler();
        return handler.bind(clazz,address,port);
    }
}
