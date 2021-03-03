package mineopoly_three.competition;

import java.awt.*;

/**
 * Distance class to organize code and calculate Manhattan distances between points
 */
public class Distance {
    private final int xDistance;
    private final int yDistance;
    private final int magnitude;

    public Distance(int x, int y) {
        xDistance = x;
        yDistance = y;
        magnitude = Math.abs(xDistance) + Math.abs(yDistance);
    }

    /**
     * Returns the distance between two given points
     * @param firstLocation location of the first point
     * @param secondLocation location of the second point
     * @return a distance variable that contains different distance stats
     */
    public static Distance getDistanceBetweenPoints(Point firstLocation, Point secondLocation) {

        int xDist = (int)(firstLocation.getX() - secondLocation.getX());
        int yDist = (int)(firstLocation.getY() - secondLocation.getY());

        return new Distance(xDist, yDist);
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

}