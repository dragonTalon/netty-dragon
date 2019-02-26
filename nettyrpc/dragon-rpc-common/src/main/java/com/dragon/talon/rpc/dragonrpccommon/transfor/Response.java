package com.dragon.talon.rpc.dragonrpccommon.transfor;

/**
 * 结果响应
 * @author dragonboy 
 */
public class Response {
    private String requestId;
    
    private boolean success;
    
    private Object object;
    
    private Throwable throwable;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
