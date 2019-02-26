package com.dragon.talon.rpc.dragonrpccommon.common;

/**
 * @author dragonboy 
 */
public enum TypeEnum {
    /**
     * 请求
     */
    request(0),
    /**
     * 响应
     */
    response(1);

    private int value;
    
    TypeEnum(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}
