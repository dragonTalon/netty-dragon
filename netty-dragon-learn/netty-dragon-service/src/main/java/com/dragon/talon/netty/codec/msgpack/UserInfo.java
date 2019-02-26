package com.dragon.talon.netty.codec.msgpack;

import org.msgpack.annotation.Message;

import java.nio.ByteBuffer;
@Message
public class UserInfo {
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

    public void build(String userName, int userID) {
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
