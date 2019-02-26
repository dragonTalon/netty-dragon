package com.dragon.talon.netty.codec.protostuff;

public class StuffInfo {
    private String info;
    
    private int infoID;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getInfoID() {
        return infoID;
    }

    public void setInfoID(int infoID) {
        this.infoID = infoID;
    }

    @Override
    public String toString() {
        return "StuffInfo{" +
                "info='" + info + '\'' +
                ", infoID=" + infoID +
                '}';
    }
}
