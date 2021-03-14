package com.example.coffebasemanager;

public class edititem {
    public String price,itemname;
    public  int Deactivate;

    public edititem(String price, String itemname, int deactivate) {
        this.price = price;
        this.itemname = itemname;
        Deactivate = deactivate;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public int getDeactivate() {
        return Deactivate;
    }

    public void setDeactivate(int deactivate) {
        Deactivate = deactivate;
    }
}
