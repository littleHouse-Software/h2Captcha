package com.littleHouse.h2Captcha;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlParser {

    public static final int EMPTY = 0;
    public static final int I_AM_HUMAN = 1;
    public static final int TILES = 2;
    public static final int BTN_HUMAN = 0;
    public static final int BTN_SEND = 1;
    public static final int BTN_REFRESH = 2;
    private int result = EMPTY;
    private int redLine = Integer.MAX_VALUE;
    private final Point[] points = {new Point(), new Point(), new Point()};
    private final ArrayList<Bounds> boundsList = new ArrayList<>();
    private String hintText = "";
    private String btnSendText = "";
    private boolean tryAgain = false;
    private boolean unsolvable = false;

    public XmlParser(File file) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            document.getDocumentElement().normalize();
            Element element = document.getDocumentElement();
            parseNode(element, 0);
        } catch (Exception e) {
            System.out.println("XmlParser.XmlParser: " + e);
        }
    }

    private void addBounds(String stBounds) {
        if (stBounds.isEmpty()) return;
        ArrayList<Integer> boundArray = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(stBounds);
        while (matcher.find()) boundArray.add(Integer.parseInt(matcher.group()));
        if (boundArray.size() != 4) return;
        Bounds bounds = new Bounds(boundArray.get(0), boundArray.get(1), boundArray.get(2), boundArray.get(3));
        boundsList.add(bounds);
    }

    private void setRedLine(String stBounds) {
        if (stBounds.isEmpty()) return;
        ArrayList<Integer> boundArray = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(stBounds);
        while (matcher.find()) boundArray.add(Integer.parseInt(matcher.group()));
        if (boundArray.size() != 4) return;
        redLine = boundArray.get(1);
    }

    private void parseNode(Element element, int depth) {
        addBounds(element.getAttribute("bounds"));
        String attributeText = element.getAttribute("text");
        String attributeClass = element.getAttribute("class");
        if (result == I_AM_HUMAN) return;

        if (attributeText.contains("I am human") && attributeClass.contains("android.widget.CheckBox")) {
            setPoint(element.getAttribute("bounds"), BTN_HUMAN);
            result = I_AM_HUMAN;
            return;
        }

        if (attributeClass.contains("android.widget.TextView")) {
            if (attributeText.contains("Please click") || attributeText.contains("Select all animals") || attributeText.contains("Count the animals") ||
                attributeText.contains("Click the images") || attributeText.contains("Please select") || attributeText.contains("Select the images")) hintText = attributeText;
            if (attributeText.contains("Please try again.")) tryAgain = true;
            if (attributeText.contains("Please drag")) unsolvable = true;
        }

        if (attributeClass.contains("android.widget.Button")) {
            if (attributeText.contains("Refresh Challenge.")) {
                setPoint(element.getAttribute("bounds"), BTN_REFRESH);
            }
            if (attributeText.contains("Select a language")) setRedLine(element.getAttribute("bounds"));
            if (attributeText.contains("Verify Answers") || attributeText.contains("Next Challenge") || attributeText.contains("Skip Challenge")) {
                setPoint(element.getAttribute("bounds"), BTN_SEND);
                btnSendText = attributeText;
                result = TILES;
            }
        }

        NodeList nodeList = element.getChildNodes();
        for (int i=0; i< nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) parseNode((Element) node, depth+1);
        }
    }

    public int getResult() {
        return result;
    }

    public Point getPoint(int btnType) {
        return points[btnType];
    }

    private void setPoint(String stBounds, int btnType) {
        if (stBounds.isEmpty()) return;
        ArrayList<Integer> boundArray = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(stBounds);
        while (matcher.find()) boundArray.add(Integer.parseInt(matcher.group()));
        points[btnType].x = boundArray.get(0) + (boundArray.get(2)-boundArray.get(0)) / 2;
        points[btnType].y = boundArray.get(1) + (boundArray.get(3)-boundArray.get(1)) / 2;
    }

    public ArrayList<Bounds> getBoundsList() {
        return boundsList;
    }

    public String getHintText() {
        //Logger.addLine("XmlParser.getHintText: " + hintText);
        return hintText;
    }

    public boolean hasTryAgain() {
        //Logger.addLine("XmlParser.hasTryAgain: " + tryAgain);
        return tryAgain;
    }

    public String getBtnSendText() {
        //Logger.addLine("XmlParser.getBtnSendText: " + btnSendText);
        return btnSendText;
    }

    public int getRedLine() {
        return redLine;
    }

    public boolean isUnsolvable() {
        return unsolvable;
    }
}
