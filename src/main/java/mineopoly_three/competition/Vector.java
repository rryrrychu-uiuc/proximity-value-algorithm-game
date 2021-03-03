package mineopoly_three.competition;

import java.awt.*;

public class Vector {

    private final double xComponent;
    private final double yComponent;
    private final double magnitude;

    public Vector(double x, double y) {
        xComponent = x;
        yComponent = y;
        magnitude = Math.hypot(xComponent, yComponent);
    }

    /**
     * Gets the angle between two points given a reference point
     * @param referencePoint the point where the reference vertical vector is location
     * @param target the point the angle is determined to
     * @return a double representing the angle between the reference point and target poing
     */
    public static double getAngleFromReference(Point referencePoint, Point target) {

        Vector referenceVector = new Vector(0,1);
        double xComp = target.getX()-referencePoint.getX();
        double yComp = target.getY()-referencePoint.getY();
        Vector targetVector = new Vector(xComp, yComp);

        return Vector.getAngleBetweenVectors(referenceVector, targetVector);
    }

    /**
     * Gets the angle between two vectors
     * @param reference the starting vector to get the angle from
     * @param target the ending vector to get the angle to
     * @return a double representing the angle between the reference vector and the target vector
     */
    public static double getAngleBetweenVectors(Vector reference, Vector target) {

        double angleToReturn = 0;
        double dotProduct = Vector.getDotProduct(reference, target);
        double magnitudeProduct = reference.getSize() * target.getSize();

        angleToReturn += Math.acos(dotProduct/magnitudeProduct)*(180/Math.PI);

        if(target.getXComponent() - reference.getXComponent() > 0) {
            angleToReturn = 360 - angleToReturn;
        }

        return angleToReturn;
    }

    /**
     * Determines the dot product of two vectors
     * @param reference the first vector
     * @param target the second vector
     * @return the sum of the product of the components of the vectors
     */
    public static double getDotProduct(Vector reference, Vector target) {

        double xProduct = reference.getXComponent() * target.getXComponent();
        double yProduct = reference.getYComponent() * target.getYComponent();

        return xProduct + yProduct;
    }

    public double getXComponent() {
        return xComponent;
    }

    public double getYComponent() {
        return yComponent;
    }

    public double getSize() {
        return magnitude;
    }

    @Override
    public boolean equals(Object o) {

        if(o instanceof Vector targetVector) {
            return magnitude == targetVector.magnitude;
        }

        return false;
    }
}