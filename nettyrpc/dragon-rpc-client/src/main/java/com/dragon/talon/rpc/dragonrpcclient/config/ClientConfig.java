package com.dragon.talon.rpc.dragonrpcclient.config;

import com.dragon.talon.rpc.dragonrpcclient.proxy.ProxyFactory;
import com.dragon.talon.rpc.dragonrpcclient.service.DemoClient;
import com.dragon.talon.rpc.dragonrpccommon.annotation.Consumer;
import com.dragon.talon.rpc.dragonrpccommon.api.DemoApi;
import com.dragon.talon.rpc.dragonrpccommon.serializer.SerializerUtils;
import com.dragon.talon.rpc.dragonrpccommon.zookeeper.DragonZk;
import com.google.common.collect.Maps;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 客户端配置
 *
 * @author dragonboy
 */
@Configuration
public class ClientConfig {
    @Autowired
    private ApplicationContext application;

    @Value("${dragon.server.address}")
    private String host;
    @Value("${dragon.server.port}")
    private int port;
    @Value("dragon.zk.service.timeout")
    private int timeout;
    @Value("dragon.zk.service.loacl")
    private String loacl;
    /**
     * 存放所有实现的proxy对象
     * 就和ioc一样嘛～
     */
    private Map<Class<?>, Object> proxyMap = Maps.newConcurrentMap();

    private DragonZk dragonZk;

    @Bean
    public BeanPostProcessor beanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                Class<?> objClz;
                if (AopUtils.isAopProxy(bean)) {
                    objClz = AopUtils.getTargetClass(bean);
                } else {
                    objClz = bean.getClass();
                }

                try {
                    for (Field field : objClz.getDeclaredFields()) {
                        Consumer consumer = field.getAnnotation(Consumer.class);
                        if (consumer != null) {
                            if (dragonZk == null) {
                                dragonZk = DragonZk.newInstance(loacl,timeout);
                            }
                            List<String> children = dragonZk.getZkClient().getChildren(DragonZk.PREFIX);
                            if (CollectionUtils.isEmpty(children)) {
                                break;
                            }
                            Object obj = proxyMap.get(field.getType());
                            if (Objects.isNull(obj)) {
                                byte[] bytes = dragonZk.getZkClient().readData(DragonZk.PREFIX + "/" + field.getType().getName());
                                String local = SerializerUtils.deserialize(bytes, String.class);
                                String[] split = local.split(":");
                                obj = ProxyFactory.create(new Class[]{field.getType()}, split[0], Integer.valueOf(split[1]));
                                proxyMap.put(field.getType(), obj);
                            }
                            field.setAccessible(true);
                            field.set(bean, obj);
                        }
                        field.setAccessible(false);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return bean;
            }
        };
    }


}
