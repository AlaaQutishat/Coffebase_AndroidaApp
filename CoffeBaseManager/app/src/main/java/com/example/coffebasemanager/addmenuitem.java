package com.example.coffebasemanager;

public class addmenuitem {
    String itemname,calories,category,price ;
    int Deactivate;

    public addmenuitem( String itemname, String calories, String category , String price , int deactivate) {

        this.itemname = itemname;
        this.calories = calories;
        this.category  = category;
        this.price = price;
        this.Deactivate=deactivate;
    }



    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getcategory() {
        return category ;
    }

    public void setcategory(String category) {
        this.category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDeactivate(int deactivate) {
        Deactivate = deactivate;
    }


    public int getDeactivate() {
        return Deactivate;
    }
}
