package com.example.icarus.lorawan.ListView;

import java.util.List;

public class Device {
    private String deveui;
    private String devname;
    private String status;
    private String lastruntime;
    private String type;

    public Device(String deveui, String devname, String status, String lastruntime, String type) {
        this.deveui = deveui;
        this.devname = devname;
        this.status = status;
        this.lastruntime = lastruntime;
        this.type = type;
    }

    public String getDeveui() {
        return deveui;
    }

    public void setDeveui(String deveui) {
        this.deveui = deveui;
    }

    public String getDevname() {
        return devname;
    }

    public void setDevname(String devname) {
        this.devname = devname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastruntime() {
        return lastruntime;
    }

    public void setLastruntime(String lastruntime) {
        this.lastruntime = lastruntime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
