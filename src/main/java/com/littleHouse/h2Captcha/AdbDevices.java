package com.littleHouse.h2Captcha;

import java.util.ArrayList;

public class AdbDevices {

    private final ArrayList<AdbDevice> deviceList = new ArrayList<>();

    public void addDevice(String name, ArrayList<String> descriptionList) {
        if (name.isEmpty()) return;
        deviceList.add(new AdbDevice(name, descriptionList));
    }

    public AdbDevice getDevice(int index) {
        return deviceList.get(index);
    }

    public ArrayList<AdbDevice> getDeviceList() {
        return deviceList;
    }

    public AdbDevices() {
    }

}
