package com.littleHouse.h2Captcha;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DeviceList extends JPanel implements DeviceClickListener {

    private DeviceListChangeListener deviceListChangeListener;
    private final ArrayList<DeviceElement> deviceElementList = new ArrayList<>();
    private final PanelPreview panelPreview;
    public static int selectedIndex = -1;

    public DeviceList(LayoutManager layoutManager, PanelPreview panelPreview) {
        super(layoutManager);
        this.panelPreview = panelPreview;
        setBackground(Color.WHITE);
    }

    public void btnFindClick() {
        close();
        deviceElementList.clear();
        selectedIndex = -1;
        AdbDevices adbDevices = DebugBridge.getDevices();
        removeAll();
        for (AdbDevice adbDevice: adbDevices.getDeviceList())
            addDeviceElement(adbDevice.getName(), adbDevice.getDescriptionList().toString());
        if (deviceListChangeListener != null) deviceListChangeListener.onChange();
    }

    private void addDeviceElement(String stName, String stDesc) {
        DeviceElement deviceElement = new DeviceElement(deviceElementList.size(), stName, stDesc, new GridBagLayout());
        deviceElement.setDeviceClickListener(this);
        add(deviceElement, new GridBagConstraints(0, deviceElementList.size(),2,1,1.0,0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
        deviceElementList.add(deviceElement);
    }

    @Override
    public void onDeviceClick(int index) {
        selectedIndex = index;
        for (int i=0; i<deviceElementList.size(); i++)
            deviceElementList.get(i).setSelected(i == index);
        String deviceName = deviceElementList.get(index).getDeviceName();
        new Thread(() -> panelPreview.setImage(DebugBridge.getScreenshot(index, deviceName))).start();
    }

    public DeviceElement getSelectedDeviceElement() {
        return deviceElementList.get(selectedIndex);
    }

    public void setDeviceListChangeListener(DeviceListChangeListener deviceListChangeListener) {
        this.deviceListChangeListener = deviceListChangeListener;
    }

    public void close() {
        for (DeviceElement deviceElement: deviceElementList) deviceElement.close();
    }

}
