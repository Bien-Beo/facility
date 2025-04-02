package com.utc2.facilityui.model;

public class ButtonNav {
    private String name;
    private String ImageSrc;
    public ButtonNav(){}
    public ButtonNav(String name, String imageSrc) {
        this.name = name;
        this.ImageSrc = imageSrc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageSrc() {
        return ImageSrc;
    }

    public void setImageSrc(String imageSrc) {
        ImageSrc = imageSrc;
    }
}
//test