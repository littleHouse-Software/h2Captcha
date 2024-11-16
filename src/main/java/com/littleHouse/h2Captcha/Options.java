package com.littleHouse.h2Captcha;

import java.io.*;

public class Options {

    private static final String[] record = {""};

    public static void saveOptions() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(MainFrame.runDir + "Options.h2c"))) {
            for (String s : record) {
                bufferedWriter.write(s);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            System.out.println("Options.saveOptions: " + e);
        }
    }

    public static void loadOptions() {
        File file = new File(MainFrame.runDir + "Options.h2c");
        if (!file.exists()) return;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String stLine;
            for (int i=0; i<record.length; i++) {
                stLine = bufferedReader.readLine();
                if (stLine != null) record[i] = stLine;
            }
        } catch (IOException e) {
            System.out.println("Options.loadOptions: " + e);
        }
    }

    public static String getApiKey() {
        return record[0];
    }

    public static void setApiKey(String apiKey) {
        record[0] = apiKey;
    }

}
