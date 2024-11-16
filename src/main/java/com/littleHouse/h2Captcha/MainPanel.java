package com.littleHouse.h2Captcha;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainPanel extends JPanel {

    private final DeviceList deviceList;
    public JLabel labelBalance = new JLabel("$0.00");
    public static JTextField textApiKey = new JTextField();
    public static JTextArea textLog = new JTextArea();
    private final ScheduledExecutorService scheduledExecutorService;

    public MainPanel(LayoutManager layoutManager) {
        super(layoutManager);
        Options.loadOptions();
        textApiKey.setEditable(false);
        textApiKey.setText(Options.getApiKey());
        textLog.setEditable(false);
        textLog.setText("Program started.");
        DefaultCaret defaultCaret = (DefaultCaret) textLog.getCaret();
        defaultCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane jScrollPaneInfo = new JScrollPane(textLog);
        jScrollPaneInfo.setPreferredSize(new Dimension(0,150));
        jScrollPaneInfo.setMinimumSize(new Dimension(0,150));
        PanelPreview panelPreview = new PanelPreview();
        deviceList = new DeviceList(new GridBagLayout(), panelPreview);
        JButton btnApiKey = new JButton("Api key");
        btnApiKey.addActionListener(e -> btnApiKeyClick());
        JButton btnFind = new JButton("Emulators");
        btnFind.addActionListener(e -> deviceList.btnFindClick());
        add(labelBalance, new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10,10,10,10),0,0));
        add(textApiKey, new GridBagConstraints(1,0,1,1,1,0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10,0,10,10),0,0));
        add(btnApiKey, new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10,0,10,10),0,0));
        add(deviceList, new GridBagConstraints(0,1,2,1,0,0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,10,10,10),0,0));
        add(btnFind, new GridBagConstraints(2,1,1,1,0,0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,10,10),0,0));
        add(panelPreview, new GridBagConstraints(0,2,3,1,1.0,1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,10,10,10),0,0));
        add(jScrollPaneInfo, new GridBagConstraints(0,3,3,1,1.0,0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,10,10,10),0,0));
        deviceList.setDeviceListChangeListener(() -> { revalidate(); repaint(); });
        deviceList.btnFindClick();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!Options.getApiKey().isEmpty()) labelBalance.setText(new CaptchaSender("").getBalance());
        },0,5, TimeUnit.MINUTES);
    }

    private void btnApiKeyClick() {
        String apikey = JOptionPane.showInputDialog(this, "Api key", Options.getApiKey());
        if (apikey == null || apikey.isEmpty()) return;
        Options.setApiKey(apikey);
        Options.saveOptions();
        textApiKey.setText(apikey);
        new Thread(() -> labelBalance.setText(new CaptchaSender("").getBalance())).start();
    }

    public static void addInfo(String stInfo) {
        String[] stLines = textLog.getText().split("\n");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0; i<stLines.length; i++)
            if (stLines.length<100 || i>0) stringBuilder.append(stLines[i]).append("\n");
        stringBuilder.append(stInfo).append("\n");
        textLog.setText(stringBuilder.toString());
    }

    public void close() {
        scheduledExecutorService.shutdownNow();
        deviceList.close();
    }

}
