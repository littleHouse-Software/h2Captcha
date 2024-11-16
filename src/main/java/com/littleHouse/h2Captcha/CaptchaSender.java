package com.littleHouse.h2Captcha;

import com.twocaptcha.TwoCaptcha;
import com.twocaptcha.captcha.Coordinates;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CaptchaSender {

    public static final int STATUS_ERROR = -1;
    public static final int STATUS_READY = 0;
    public static final int STATUS_SENT = 1;
    public static final int STATUS_SOLVED = 2;
    public static final int STATUS_REPORT = 3;
    private final TwoCaptcha twoCaptcha;
    private final ArrayList<Point> pointList = new ArrayList<>();
    private long startTime;
    private int status = STATUS_READY;
    private String captchaId = "";
    private final String deviceName;

    public CaptchaSender(String deviceName) {
        this.deviceName = deviceName;
        twoCaptcha = new TwoCaptcha();
        twoCaptcha.setSoftId(4735);
        twoCaptcha.setDefaultTimeout(60);
        twoCaptcha.setRecaptchaTimeout(60);
        twoCaptcha.setPollingInterval(5);
        twoCaptcha.setApiKey(Options.getApiKey());
    }

    public void sendCaptcha(File file, String hintText) {
        Coordinates coordinatesCaptcha = new Coordinates(file);
        coordinatesCaptcha.setSoftId(4735);
        coordinatesCaptcha.setLang("en");
        coordinatesCaptcha.setHintText(hintText);
        new Thread(()->{
            try {
                startTime = System.currentTimeMillis();
                status = STATUS_SENT;
                MainPanel.addInfo(deviceName + ": Captcha sent");
                twoCaptcha.solve(coordinatesCaptcha);
                captchaId = coordinatesCaptcha.getId();
                setPoints(coordinatesCaptcha.getCode());
                status = STATUS_SOLVED;
            } catch (Exception e) {
                long elapsedTime = (System.currentTimeMillis()-startTime) / 1000;
                MainPanel.addInfo(deviceName + ": Error: " + e);
                status = STATUS_ERROR;
            }
        }).start();
    }

    public void report(boolean correct) {
        if (captchaId.isEmpty()) return;
        try {
            twoCaptcha.report(captchaId, correct);
            MainPanel.addInfo(deviceName + ": Report sent = " + (correct ? "valid" : "invalid"));
        } catch (Exception e) {
            System.out.println("CaptchaSender.report: " + e);
        }
        status = STATUS_READY;
    }

    private void setPoints(String stResult) {
        long elapsedTime = (System.currentTimeMillis()-startTime) / 1000;
        MainPanel.addInfo(deviceName + ": Captcha solved");
        pointList.clear();
        try {
            if (stResult.isEmpty()) throw new Exception("Result is empty");
            ArrayList<Integer> boundList = new ArrayList<>();
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(stResult);
            while (matcher.find()) boundList.add(Integer.parseInt(matcher.group()));
            if (boundList.size() % 2 != 0) throw new Exception("Missing coordinate");
            for (int i=0; i<boundList.size()/2; i++)
                pointList.add(new Point(boundList.get(i*2), boundList.get(i*2+1)));
        } catch (Exception e) {
            System.out.println("CaptchaSender.setPoints: " + e);
        }
    }

    public String getBalance() {
        try {
            return String.format("$%,.2f", twoCaptcha.balance());
        } catch (Exception e) {
            MainPanel.addInfo("Couldn't get balance: " + e.getMessage());
            return "$0.00";
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public ArrayList<Point> getPointList() {
        return pointList;
    }

}
