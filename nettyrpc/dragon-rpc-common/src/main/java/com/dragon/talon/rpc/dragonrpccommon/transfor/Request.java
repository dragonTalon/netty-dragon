package com.dragon.talon.rpc.dragonrpccommon.transfor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

/**
 * 传输请求
 *
 * @author dragonboy
 */
public class Request<T> {
    /**
     * 请求id
     */
    private String requestId;
    /**
     * 类类型
     */
    private String clazz;
    /**
     * 调用的方法
     */
    private String methodName;

    /**
     * 参数
     */
    private Class<?>[] paramClass;
    private Object[] arg;


    public static <T> Request newInstance(Method method, String clazz, Object[] arg) {
        Request request = new Request();
        request.setRequestId(UUID.randomUUID().toString());
        String className = method.getDeclaringClass().getName();
        request.setClazz(className);
        request.setMethodName(method.getName());
        request.setParamClass(method.getParameterTypes());
        request.setArg(arg);
        return request;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamClass() {
        return paramClass;
    }

    public void setParamClass(Class<?>[] paramClass) {
        this.paramClass = paramClass;
    }

    public Object[] getArg() {
        return arg;
    }

    public void setArg(Object[] arg) {
        this.arg = arg;
    }
}
