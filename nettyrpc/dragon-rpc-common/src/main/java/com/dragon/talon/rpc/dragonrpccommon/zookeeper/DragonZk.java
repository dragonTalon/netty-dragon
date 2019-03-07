package com.dragon.talon.rpc.dragonrpccommon.zookeeper;



import com.dragon.talon.rpc.dragonrpccommon.serializer.SerializerUtils;
import com.github.zkclient.ZkClient;

import java.util.List;

/**
 * zk服务连接
 *
 * @author dragonboy
 */
public class DragonZk {
    public static final String PREFIX = "/dragonRpc";

    public static final String SPLIT = "/";

    private ZkClient zkClient;

    private String local = "127.0.0.1:2181";

    private int timeout = 1000;

    private DragonZk(String local, int timeout) {
        this.local = local;
        if (timeout > 0) {
            this.timeout = timeout;
        }
        if (zkClient == null) {
            zkClient = new ZkClient(local, timeout);
        }
    }

    public static DragonZk newInstance(String local, int timeout) {
        return new DragonZk(local, timeout);
    }

    public static DragonZk newInstance(String local) {
        return newInstance(local, 5000);
    }

    public static DragonZk newInstance() {
        return newInstance("127.0.0.1:2181");
    }

    public ZkClient getZkClient() {
        return zkClient;
    }


    public void registZkForService(String address, String serviceName) {
        String path = PREFIX + SPLIT + serviceName ;
        byte[] serialize = SerializerUtils.serialize(address);
        if (!zkClient.exists(path)) {
            zkClient.createPersistent(path,true);
            zkClient.writeData(path,serialize);
        }else {
            zkClient.writeData(path,serialize);
        }
    }

    public static void main(String[] args) {
        final DragonZk dragonZk = DragonZk.newInstance();
        System.out.println(dragonZk.getZkClient().getChildren(PREFIX));
        System.out.println(SerializerUtils.deserialize(dragonZk.getZkClient().readData
                ("/dragonRpc/com.dragon.talon.rpc.dragonrpccommon.api.DemoApi"),String.class));
    }
}
