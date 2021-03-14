package com.example.coffeeuser;

public class menuitemlist {

    public String price,itemname;
    public String image ; public  int calories, x;
    public String rate;

    public menuitemlist(String price, String itemname, String image, int calories) {
        this.price = price;
        this.itemname = itemname;
        this.image = image;
        this.calories = calories;
    }
    public menuitemlist(String price, String itemname, String image, int calories,int x) {
        this.price = price;
        this.itemname = itemname;
        this.image = image;
        this.calories = calories;
        this.x=x;
    }
    public menuitemlist(String price, String itemname, String image, int calories, String rate) {
        this.price = price;
        this.itemname = itemname;
        this.image = image;
        this.calories = calories;
        this.rate = rate;
    }
    public menuitemlist(String price, String itemname, String image, int calories, String rate,int x) {
        this.price = price;
        this.itemname = itemname;
        this.image = image;
        this.calories = calories;
        this.rate = rate;
        this.x=x;
    }
    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }
}
