package com.example.coffebasemanager;

public class orderinfo {
   public String name,seatid,time;

    public orderinfo(String name, String seatid, String time) {
        this.name = name;
        this.seatid = seatid;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeatid() {
        return seatid;
    }

    public void setSeatid(String seatid) {
        this.seatid = seatid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
