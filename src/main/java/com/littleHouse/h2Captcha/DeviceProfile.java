package com.littleHouse.h2Captcha;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

public class DeviceProfile {

    public static final int STATUS_SCAN = 0;
    public static final int STATUS_SENT = 1;
    private int status;
    private final String deviceName;

    private boolean running = false;
    private String hintText = "";
    private String btnSendText = "";
    private CaptchaSender captchaSender;
    private XmlParser xmlParser;
    private Bounds croppedBounds;
    private final File dumpFile;
    private final File screenFile;
    private File croppedFile;
    private int multipleChallenge = 0;
    private DeviceProfileStopListener deviceProfileStopListener;
    private long lastClickTime = 0;
    private String balance = "$0.00";

    public DeviceProfile(int index, String deviceName) {
        this.deviceName = deviceName;
        dumpFile = new File(MainFrame.runDir + deviceName + ".xml");
        screenFile = new File(MainFrame.runDir + deviceName + ".jpg");
    }

    private void checkElapsedTime() {
        if (captchaSender.getStatus() == CaptchaSender.STATUS_SENT) {
            long elapsedTime = (System.currentTimeMillis()-captchaSender.getStartTime()) / 1000;
            MainPanel.addInfo(deviceName + ": Waiting result " + elapsedTime);
        }
    }

    private void sendImage() {
        status = STATUS_SENT;
        if (multipleChallenge>0) multipleChallenge--;
        ArrayList<Bounds> boundsList = xmlParser.getBoundsList();
        boundsList.sort(Comparator.comparingInt(Bounds::getSize));
        croppedBounds = boundsList.get(boundsList.size()-12);
        DebugBridge.saveScreenshot(deviceName);
        if (screenFile.exists()) {
            File archiveFolder = new File(MainFrame.runDir + "Sent");
            archiveFolder.mkdir();
            String filePath = archiveFolder.getAbsolutePath() + "\\" + deviceName + "_" + System.currentTimeMillis() + ".jpg";
            croppedFile = new File(filePath);
            ImageCropper.saveCroppedImage(croppedBounds, screenFile, croppedFile);
            captchaSender = new CaptchaSender(deviceName);
            captchaSender.sendCaptcha(croppedFile, xmlParser.getHintText());
            hintText = xmlParser.getHintText();
            btnSendText = xmlParser.getBtnSendText();
        }
        sleep(5000);
    }
    private void checkReport() {
        if (captchaSender.getStatus() == CaptchaSender.STATUS_SOLVED) {
            croppedFile.delete();
            DebugBridge.sendTaps(deviceName, Bounds.normalizePointList(captchaSender.getPointList(), croppedBounds.getStartPoint(), xmlParser.getRedLine()));
            DebugBridge.getUiDump(deviceName);
            xmlParser = new XmlParser(dumpFile);
            if (xmlParser.getResult()==XmlParser.TILES && xmlParser.getBtnSendText().equals("Next Challenge")) multipleChallenge = 2;
            DebugBridge.sendTap(deviceName, xmlParser.getPoint(XmlParser.BTN_SEND));
            sleep(5000);
            captchaSender.setStatus(CaptchaSender.STATUS_REPORT);
            status = STATUS_SCAN;
        }
        if (captchaSender.getStatus() == CaptchaSender.STATUS_ERROR) {
            croppedFile.delete();
            DebugBridge.sendTap(deviceName, xmlParser.getPoint(XmlParser.BTN_REFRESH));
            sleep(5000);
            captchaSender.setStatus(CaptchaSender.STATUS_READY);
            status = STATUS_SCAN;
        }
    }

    private void sendCaptchaReport() {
        if (captchaSender.getStatus() != CaptchaSender.STATUS_REPORT) return;
        if (multipleChallenge == 0) captchaSender.report(!xmlParser.hasTryAgain());
        if (multipleChallenge == 1 && !xmlParser.hasTryAgain()) {
            captchaSender.report(true);
            multipleChallenge = 0;
        }
    }

    private void evaluateDumpResult(int result) {
        switch (result) {
            case XmlParser.EMPTY:
                sendCaptchaReport();
                hintText = "";
                btnSendText = "";
                multipleChallenge = 0;
                status = STATUS_SCAN;
                break;
            case XmlParser.I_AM_HUMAN:
                sendCaptchaReport();
                MainPanel.addInfo(deviceName + ": 'I am Human' found");
                captchaSender = new CaptchaSender(deviceName);
                if (System.currentTimeMillis()>lastClickTime+5000) {
                    DebugBridge.sendTap(deviceName, xmlParser.getPoint(XmlParser.BTN_HUMAN));
                    lastClickTime = System.currentTimeMillis();
                }
                hintText = "";
                btnSendText = "";
                multipleChallenge = 0;
                status = STATUS_SCAN;
                break;
            case XmlParser.TILES:
                if (!hintText.equals(xmlParser.getHintText()) || !btnSendText.equals(xmlParser.getBtnSendText())) {
                    sendCaptchaReport();
                    if (xmlParser.isUnsolvable()) {
                        DebugBridge.sendTap(deviceName, xmlParser.getPoint(XmlParser.BTN_REFRESH));
                        sleep(5000);
                        captchaSender.setStatus(CaptchaSender.STATUS_READY);
                        status = STATUS_SCAN;
                    } else sendImage();
                }
                if (status == STATUS_SENT) checkReport();
                break;
        }
    }

    public void start() {
        running = true;
        hintText = "";
        btnSendText = "";
        captchaSender = new CaptchaSender(deviceName);
        status = STATUS_SCAN;
        new Thread(() -> {
            while (running) {
                DebugBridge.getUiDump(deviceName);
                xmlParser = new XmlParser(dumpFile);
                evaluateDumpResult(xmlParser.getResult());
                checkElapsedTime();
            }
            deleteFiles();
            if (deviceProfileStopListener != null) deviceProfileStopListener.onStop();
        }).start();
    }

    public void stop() {
        running = false;
    }

    private void deleteFiles() {
        screenFile.delete();
        dumpFile.delete();
    }

    public boolean isRunning() {
        return running;
    }

    private void sleep(long millis) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis()<startTime+millis) {
            if (!running) break;
        }
    }

    public void setDeviceProfileStopListener(DeviceProfileStopListener deviceProfileStopListener) {
        this.deviceProfileStopListener = deviceProfileStopListener;
    }

}
