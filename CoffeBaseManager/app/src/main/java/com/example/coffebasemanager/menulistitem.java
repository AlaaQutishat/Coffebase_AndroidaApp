package com.example.coffebasemanager;


public class menulistitem {

    public String price,itemname;
 public String image ; public  int calories;

    public menulistitem(String price, String itemname, String image, int calories) {
        this.price = price;
        this.itemname = itemname;
        this.image = image;
        this.calories = calories;
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
}
