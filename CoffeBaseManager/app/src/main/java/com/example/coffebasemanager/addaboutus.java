package com.example.coffebasemanager;

public class addaboutus {
    String url,phonee,fromtime,totime,study,parking,smoke;
    int fromm,too,seat;

    public addaboutus(String url, String phonee, String fromtime, String totime, String study, String parking, String smoke, int seat, int fromm, int too) {
        this.url = url;
        this.phonee = phonee;
        this.fromtime = fromtime;
        this.totime = totime;
        this.study = study;
        this.parking = parking;
        this.smoke = smoke;
        this.seat = seat;
        this.fromm = fromm;
        this.too = too;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhonee() {
        return phonee;
    }

    public void setPhonee(String phonee) {
        this.phonee = phonee;
    }

    public String getFromtime() {
        return fromtime;
    }

    public void setFromtime(String fromtime) {
        this.fromtime = fromtime;
    }

    public String getTotime() {
        return totime;
    }

    public void setTotime(String totime) {
        this.totime = totime;
    }

    public String getStudy() {
        return study;
    }

    public void setStudy(String study) {
        this.study = study;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public String getSmoke() {
        return smoke;
    }

    public void setSmoke(String smoke) {
        this.smoke = smoke;
    }

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }

    public int getFromm() {
        return fromm;
    }

    public void setFromm(int fromm) {
        this.fromm = fromm;
    }

    public int getToo() {
        return too;
    }

    public void setToo(int too) {
        this.too = too;
    }
}
