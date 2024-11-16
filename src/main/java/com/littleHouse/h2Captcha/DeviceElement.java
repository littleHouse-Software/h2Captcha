package com.littleHouse.h2Captcha;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DeviceElement extends JPanel {

    private final int index;
    private final DeviceProfile deviceProfile;
    private DeviceClickListener deviceClickListener;
    private final JButton btnConnect;
    private final JTextField jTextFieldName;
    private final String deviceName;
    private boolean profileStopped = true;

    public DeviceElement(int index, String stName, String stDesc, LayoutManager layoutManager) {
        super(layoutManager);
        this.index = index;
        this.deviceName = stName;
        Options.loadOptions();
        deviceProfile = new DeviceProfile(index, stName);
        deviceProfile.setDeviceProfileStopListener(this::deviceProfileStop);
        btnConnect = new JButton("Connect");
        btnConnect.addMouseListener(mouseAdapter);
        jTextFieldName = new JTextField();
        jTextFieldName.setBorder(null);
        jTextFieldName.setText(stName);
        jTextFieldName.setEnabled(false);
        jTextFieldName.setBackground(Color.WHITE);
        jTextFieldName.setDisabledTextColor(Color.BLACK);
        jTextFieldName.addMouseListener(mouseAdapter);
        add(jTextFieldName, new GridBagConstraints(0,0,1,1,1.0,0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,4,0,4),0,0));
        add(btnConnect, new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2,4,2,4),0,0));
        addMouseListener(mouseAdapter);
        setBackground(Color.WHITE);
    }

    private void deviceProfileStop() {
        btnConnect.setText("Start");
        profileStopped = true;
    }

    MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            if (deviceClickListener != null) deviceClickListener.onDeviceClick(index);
            if (e.getSource().getClass().getName().contains("JButton")) toggle();
        }
    };

    private void toggle() {
        if (deviceProfile.isRunning()) {
            profileStopped = false;
            btnConnect.setText("Disconnecting");
            deviceProfile.stop();
        }
        if (profileStopped) {
            deviceProfile.start();
            btnConnect.setText("Disconnect");
        }
    }

    public void setSelected(boolean selected) {
        setBackground(selected ? Color.LIGHT_GRAY : Color.WHITE);
        btnConnect.setBackground(selected ? Color.LIGHT_GRAY : Color.WHITE);
        jTextFieldName.setBackground(selected ? Color.LIGHT_GRAY : Color.WHITE);
    }

    public void setDeviceClickListener(DeviceClickListener deviceClickListener) {
        this.deviceClickListener = deviceClickListener;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public DeviceProfile getDeviceProfile() {
        return deviceProfile;
    }

    public void close() {
        deviceProfile.stop();
    }

}
