package com.example.coffeeuser;

public class orderlistinfo  {
     public String name,price;
    public int quintity;

    public orderlistinfo(String name, String price, int quintity) {
        this.name = name;
        this.price = price;
        this.quintity = quintity;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


}
