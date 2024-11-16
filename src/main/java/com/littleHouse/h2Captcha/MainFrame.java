package com.littleHouse.h2Captcha;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MainFrame extends JFrame {

    public static String runDir = "";
    private final MainPanel mainPanel;

    public MainFrame() throws HeadlessException {
        runDir = getRunDir();
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            System.out.println("MainFrame.MainFrame: " + e);
        }
        URL urlIcon = getClass().getResource("/resources/icon.png");
        if (urlIcon != null) {
            ImageIcon imageIcon = new ImageIcon(urlIcon);
            setIconImage(imageIcon.getImage());
        }
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(true);
        setSize(420, 500);
        setMinimumSize(new Dimension(420, 300));
        mainPanel = new MainPanel(new GridBagLayout());
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
        setTitle("h2Captcha");
        setVisible(true);
    }

    private String getRunDir() {
        StringBuilder stringBuilder = new StringBuilder(System.getProperty("java.class.path"));
        int lastIndex = stringBuilder.lastIndexOf("\\");
        if (lastIndex < 0) return "";
        stringBuilder.delete(lastIndex, stringBuilder.length());
        stringBuilder.append("\\");
        return stringBuilder.toString();
    }

    @Override
    public void dispose() {
        super.dispose();
        mainPanel.close();
    }

    public static void main(String[] args) {
        new MainFrame();
    }

}
