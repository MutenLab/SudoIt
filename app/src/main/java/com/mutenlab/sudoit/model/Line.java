package com.mutenlab.sudoit.model;

import org.opencv.core.Point;

import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.sin;


/**
 * @author Ivan Cerrate.
 */

public class Line {
    public enum Orientation {
        horizontal,
        vertical,
        fortyFiveDegree,
    }

    public Point origin;
    public Point destination;

    public Line(Point origin, Point destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public Line(Vector vector, int height, int width) {
        Point origin = new Point();
        Point destination = new Point();
        double a = cos(vector.theta), b = sin(vector.theta);
        double x0 = a * vector.rho, y0 = b * vector.rho;
        origin.x = (x0 + width * (-b));
        origin.y = (y0 + height * (a));
        destination.x = (x0 - width * (-b));
        destination.y = (y0 - height * (a));

        this.origin = origin;
        this.destination = destination;
    }

    public Orientation getOrientation() {
        if (getHeight() == getwidth())
            return Orientation.fortyFiveDegree;
        if (getHeight() > getwidth())
            return Orientation.vertical;
        return Orientation.horizontal;
    }

    private double getHeight() {
        return getMaxY() - getMinY();
    }

    private double getwidth() {
        return getMaxX() - getMinX();
    }

    public double getMinX() {
        if (origin.x < destination.x)
            return origin.x;
        return destination.x;
    }

    public double getMaxX() {
        if (origin.x > destination.x)
            return origin.x;
        return destination.x;
    }

    public double getMaxY() {
        if (origin.y > destination.y)
            return origin.y;
        return destination.y;
    }

    public double getMinY() {
        if (origin.y < destination.y)
            return origin.y;
        return destination.y;
    }

    public double getAngleFromXAxis() {
        double radAngle = atan(getHeight() / getwidth());
        double degAngle = radAngle * 180 / PI;
        return degAngle;
    }

    public Point findIntersection(Line line2) {
        //See http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
        double line1DeltaX = destination.x - origin.x;
        double line1DeltaY = destination.y - origin.y;
        double line2DeltaX = line2.destination.x - line2.origin.x;
        double line2DeltaY = line2.destination.y - line2.origin.y;

        double linesDeltaOriginX = origin.x - line2.origin.x;
        double linesDeltaOriginY = origin.y - line2.origin.y;

        double denominator = line1DeltaX * line2DeltaY - line2DeltaX * line1DeltaY;
        double numeratorT = line2DeltaX * linesDeltaOriginY - line2DeltaY * linesDeltaOriginX;

        double t = numeratorT / denominator;


        if (linesAreColinear(denominator))
            return null;


        return calculateIntersection(line1DeltaX, line1DeltaY, t);
    }

    private boolean linesAreColinear(double denominator) {
        return denominator == 0;
    }

    public Point calculateIntersection(double line1DeltaX, double line1DeltaY, double t) {
        Point intersection = new Point();
        intersection.x = origin.x + (t * line1DeltaX);
        intersection.y = origin.y + (t * line1DeltaY);
        return intersection;
    }
}