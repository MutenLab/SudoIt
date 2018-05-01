package com.mutenlab.sudoit.common;

import org.opencv.core.Point;

public class PuzzleOutLine {
    public Point bottomLeft;
    public Point bottomRight;
    public Point topLeft;
    public Point topRight;

    public Line top;
    public Line bottom;
    public Line left;
    public Line right;

    public double getSize() {
        double height = getHeight();
        double width = getWidth();
        if (height > width)
            return height;
        return width;
    }

    private double getHeight() {
        double smallestY = Double.MAX_VALUE;
        double largestY = Double.MIN_VALUE;

        Point[] points = {bottomLeft, bottomRight, topLeft, topRight};

        for (int i = 0; i < points.length; i++) {
            if (points[i].y < smallestY)
                smallestY = points[i].y;
            if (points[i].y > largestY)
                largestY = points[i].y;
        }
        return largestY - smallestY;
    }

    private double getWidth() {
        double smallestX = Double.MAX_VALUE;
        double largestX = Double.MIN_VALUE;

        Point[] points = {bottomLeft, bottomRight, topLeft, topRight};

        for (int i = 0; i < points.length; i++) {
            if (points[i].x < smallestX)
                smallestX = points[i].x;
            if (points[i].x > largestX)
                largestX = points[i].x;
        }
        return largestX - smallestX;
    }
}
