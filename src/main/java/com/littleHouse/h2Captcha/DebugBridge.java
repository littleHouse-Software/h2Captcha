package com.littleHouse.h2Captcha;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class DebugBridge {

    public static void sendTap(String deviceName, Point point) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(MainFrame.runDir + "adb.exe", "-s", deviceName, "shell", "input", "tap", String.valueOf(point.x), String.valueOf(point.y));
            Process process = processBuilder.start();
            int tapExitCode = process.waitFor();
            if (tapExitCode<0) System.out.println("DebugBridge.sendTap: " + "tapExitCode: " + tapExitCode);
            Thread.sleep(500 + (int) (Math.random()*500));
        } catch (Exception e) {
            System.out.println("DebugBridge.sendTap: " + e);
        }
    }

    public static void sendTaps(String deviceName, ArrayList<Point> pointList) {
        for (Point point: pointList) sendTap(deviceName, point);
    }

    public static BufferedImage getScreenshot(int deviceIndex, String deviceName) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder( MainFrame.runDir + "adb.exe", "-s", deviceName, "exec-out", "screencap", "-p");
            Process process = processBuilder.start();
            try (InputStream inputStream = process.getInputStream()) {
                return ImageIO.read(inputStream);
            }
        } catch (Exception e) {
            System.out.println("DebugBridge.getScreenshot: " + e);
            return null;
        }
    }

    public static void saveScreenshot(String deviceName) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder( MainFrame.runDir + "adb.exe", "-s", deviceName, "exec-out", "screencap", "-p");
            Process process = processBuilder.start();
            try (InputStream inputStream = process.getInputStream()) {
                BufferedImage png = ImageIO.read(inputStream);
                BufferedImage jpg = new BufferedImage(png.getWidth(), png.getHeight(), BufferedImage.TYPE_INT_RGB);
                jpg.createGraphics().drawImage(png, 0, 0, null);
                ImageIO.write(jpg, "jpg", new File(MainFrame.runDir + deviceName + ".jpg"));
            }
        } catch (Exception e) {
            System.out.println("DebugBridge.saveScreenshot: " + e);
        }
    }

    public static void getUiDump(String deviceName) {
        ProcessBuilder processBuilderDumpXml = new ProcessBuilder(MainFrame.runDir + "adb.exe", "-s", deviceName, "shell", "uiautomator", "dump", "/sdcard/ui_dump.xml");
        ProcessBuilder processBuilderPullXml = new ProcessBuilder(MainFrame.runDir + "adb.exe", "-s", deviceName, "pull", "/sdcard/ui_dump.xml", MainFrame.runDir + deviceName + ".xml");
        try {
            Process processDumpXml = processBuilderDumpXml.start();
            int dumpExitCode = processDumpXml.waitFor();
            if (dumpExitCode<0) System.out.println("DebugBridge.getUiDump: " + "dump exitCode: " + dumpExitCode);
            Process processPullXml = processBuilderPullXml.start();
            int pullExitCode = processPullXml.waitFor();
            if (pullExitCode<0) System.out.println("DebugBridge.getUiDump: " + "pull exitCode: " + pullExitCode);
        } catch (Exception e) {
            System.out.println("DebugBridge.getUiDump: " + e);
        }
    }

    public static AdbDevices getDevices() {
        ProcessBuilder processBuilder = new ProcessBuilder(MainFrame.runDir + "adb.exe", "devices", "-l");
        AdbDevices adbDevices = new AdbDevices();
        try {
            Process process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int exitCode = process.waitFor();
            if (exitCode<0) System.out.println("DebugBridge.getDevices: " + "exitCode = " + exitCode);
            String stLine;
            ArrayList<String> arrayList = new ArrayList<>();
            do {
                arrayList.add(stLine = bufferedReader.readLine());
            } while (stLine != null && !stLine.isEmpty());
            arrayList.remove(0);  // "List of connected devices" line
            for (String stArray: arrayList) {
                String[] fields = stArray.split(" ");
                String name = (fields.length>0) ? fields[0] : "";
                if (name.isEmpty()) continue;
                ArrayList<String> descList = new ArrayList<>();
                for (String field: fields)
                    if (!field.isEmpty()) descList.add(field);
                adbDevices.addDevice(name, descList);
            }
            MainPanel.addInfo(adbDevices.getDeviceList().size() + " emulator found.");
        } catch (Exception e) {
            System.out.println("DebugBridge.getDevices: " + e);
        }
        return adbDevices;
    }

}
