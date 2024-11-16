package com.littleHouse.h2Captcha;

import java.awt.*;
import java.util.ArrayList;

public class Bounds {

    public int left, top, right, bottom;

    public static ArrayList<Point> normalizePointList(ArrayList<Point> pointList, Point pointNormal, int redLine) {
        ArrayList<Point> pointListNormal = new ArrayList<>();
        for (Point point: pointList) {
            point.x += pointNormal.x;
            point.y += pointNormal.y;
            if (point.y<redLine-10) pointListNormal.add(point);
        }
        return pointListNormal;
    }


    public Bounds(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public int getWidth() {
        return Math.abs(right-left);
    }

    public int getHeight() {
        return Math.abs(bottom-top);
    }

    public int getSize() {
        return getWidth()*getHeight();
    }

    public Point getStartPoint() {
        return new Point(left, top);
    }

}
