package com.example.coffebasemanager;

public class salesitem {
    int count,price;
    String name;
    int totalprice;

    public salesitem(int count, int price, String name, int totalprice) {
        this.count = count;
        this.price = price;
        this.name = name;
        this.totalprice = totalprice;
    }

    public salesitem(int count, int price, String name) {
        this.count = count;
        this.price = price;
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(int totalprice) {
        this.totalprice = totalprice;
    }
}
