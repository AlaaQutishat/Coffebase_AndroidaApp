package com.example.coffeeuser;

import androidx.annotation.NonNull;

public class Model implements Comparable<Model> {
   public String Name,Image,AvarageRate;

    public Model() {
    }

    public Model(String name, String image , String avarageRate) {
        Name = name;
        Image = image;
        AvarageRate = avarageRate;
    }
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
    public String getAvarageRate() {
        return AvarageRate;
    }

    public void setAvarageRate(String avarageRate) {
        AvarageRate = avarageRate;
    }

    @NonNull
    @Override
    public String toString() {
        return "The Entity is : " +
                Name + " The Image is : " + Image;
    }

    @Override
    public int compareTo(Model o) {
        return  getAvarageRate().compareTo(o.getAvarageRate());
    }
}
