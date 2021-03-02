package mineopoly_three.strategy;

import java.awt.*;

public class Distance {
    private int xDistance;
    private int yDistance;
    private int magnitude;

    public Distance(int x, int y) {
        xDistance = x;
        yDistance = y;
        magnitude = Math.abs(xDistance) + Math.abs(yDistance);
    }

    public int getXDistance() {
        return xDistance;
    }

    public int getYDistance() {
        return yDistance;
    }

    public int getMagnitude() {
        return magnitude;
    }

    @Override
    public boolean equals(Object o) {

        if(o instanceof Distance targetDistance) {
            return magnitude == targetDistance.magnitude;
        }

        return false;
    }

    public static Distance getDistanceBetweenPoints(Point currentLocation, Point otherLocation) {

        int xDist = (int)(currentLocation.getX() - otherLocation.getX());
        int yDist = (int)(currentLocation.getY() - otherLocation.getY());

        return new Distance(xDist, yDist);
    }

}