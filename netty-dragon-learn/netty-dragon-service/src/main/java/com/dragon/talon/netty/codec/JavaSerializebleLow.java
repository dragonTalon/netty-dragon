package com.dragon.talon.netty.codec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 性能低下，占有内存巨大，不跨语言
 *
 * @author dragonboy
 */
public class JavaSerializebleLow {
    public static class UserInfo implements Serializable {
        private String userName;

        private int userID;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public void build(String userName,int userID){
            this.userName = userName;
            this.userID = userID;
        }
        
        public byte[] codec() {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            byte[] bytes = this.getUserName().getBytes();
            buffer.putInt(bytes.length);
            buffer.put(bytes);
            buffer.putInt(this.getUserID());
            buffer.flip();
            bytes = null;
            byte[] bytes1 = new byte[buffer.remaining()];
            buffer.get(bytes1);
            return bytes1;
        }

        @Override
        public String toString() {
            return "UserInfo{" +
                    "userName='" + userName + '\'' +
                    ", userID=" + userID +
                    '}';
        }
    }

    /**
     * java 序列化 与 二进制编码
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserID(11);
        userInfo.setUserName("talon");
        LocalDateTime begin = LocalDateTime.now();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(userInfo);
        os.flush();
        os.close();
        final byte[] bytes = bos.toByteArray();
        final LocalDateTime endJ = LocalDateTime.now();
        System.out.println("java Serializeble size : "+bytes.length);
        bos.close();
        System.out.println(Duration.between(begin,endJ).toNanos());
        begin = LocalDateTime.now();
        System.out.println("binary byte array serializeble length : "+userInfo.codec().length);
        System.out.println(Duration.between(begin,LocalDateTime.now()).toNanos());
        if (bytes.length>userInfo.codec().length){
            System.out.println("java serializeble low");
        }
    }
}
