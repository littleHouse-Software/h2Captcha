package com.littleHouse.h2Captcha;

import java.util.ArrayList;

public class AdbDevice {

    private String name;
    private ArrayList<String> descriptionList;

    public AdbDevice(String name, ArrayList<String> descriptionList) {
        this.name = name;
        this.descriptionList = descriptionList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getDescriptionList() {
        return descriptionList;
    }

    public void setDescriptionList(ArrayList<String> descriptionList) {
        this.descriptionList = descriptionList;
    }

}
