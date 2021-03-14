package com.example.coffebasemanager;

public class imageinfo {

    public String imageName;

    public String imageURL;

    public imageinfo() {

    }

    public imageinfo(String name, String url) {

        this.imageName = name;
        this.imageURL= url;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageURL() {
        return imageURL;
    }
}
